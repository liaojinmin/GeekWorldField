package me.geek.world.api.world


import com.google.gson.Gson
import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.event.FieldWorldCreateEvent
import me.geek.world.api.event.FieldWorldLoadEvent
import me.geek.world.common.listener.WorldListener
import me.geek.world.common.world.CacheDataPack
import me.geek.world.common.world.CacheMapsPack
import me.geek.world.io.forFile
import me.geek.world.scheduler.TaskHub
import me.geek.world.scheduler.task.WorldSpawnTask
import me.geek.world.settings.SetTings
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent
import org.bukkit.event.world.WorldLoadEvent
import taboolib.common.platform.function.unregisterListener
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.security.SecureRandom
import java.util.Random

import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
object WorldManage {

    /**
     * 运行中的世界
     * key = 世界名称
     * value = 世界
     */
    private val onlineWorld: MutableMap<String, FieldWorld> = mutableMapOf()
    private val cacheWorld: MutableList<CacheMapsPack> = mutableListOf()


    val CacheDataFile by lazy {
        val dir = File(GeekWorldField.instance.dataFolder, "cache_data")
        if (!dir.exists()) dir.mkdir()
        dir
    }

    val CacheMapsFile by lazy {
        val dir = File(GeekWorldField.instance.dataFolder, "cache_maps")
        if (!dir.exists()) dir.mkdir()
        dir
    }

    fun getFieldWorld(worldName: String): FieldWorld? {
        return onlineWorld[worldName]
    }
    fun getCacheWorld(): CacheMapsPack {
        val index = SecureRandom().apply { setSeed(1000L) }.nextInt(cacheWorld.size)
        return cacheWorld[index]
    }

    fun hasPerm(player: Player, worldName: String): Boolean {
        GeekWorldField.debug("hasPerm $worldName")
        val data = onlineWorld[worldName] ?: return false // 如果这个世界不存在
        if (player.getPlayerData().fieldWorld != null) {
            val world = player.getPlayerData().fieldWorld!!
            if (!world.isFiledWorld(worldName)) { // 如果不是自己的世界
                for (team in data.team) {
                    // 如果这个世界有这个玩家的权限
                    if (team.teamPlayerUuid == world.owner) {
                        return true
                    }
                }
                return false
            } else return true
        }
        for (team in data.team) {
            // 如果这个世界有这个玩家的权限
            if (team.teamPlayerUuid == player.uniqueId) {
                return true
            }
        }
        return false
    }


    fun FieldWorld.singOnline() {
        this.getMailWorld()?.let {
            if (!onlineWorld.containsKey(it.name)) {
                onlineWorld[it.name] = this
            }
        }
        this.getNetherWorld()?.let {
            if (!onlineWorld.containsKey(it.name)) {
                onlineWorld[it.name] = this
            }
        }
        this.getTheEndWorld()?.let {
            if (!onlineWorld.containsKey(it.name)) {
                onlineWorld[it.name] = this
            }
        }
    }

    fun FieldWorld.signOffline() {
        onlineWorld.remove(this.world)
        onlineWorld.remove(this.worldNether)
        onlineWorld.remove(this.worldTheEnd)
    }


    @Synchronized
    fun createWorld(worldName: String, type: FieldWorldType): World {
        return WorldCreator(worldName)
            .type(WorldType.NORMAL)
            .environment(type.evn)
            .createWorld() ?: error("世界创建错误: UID $worldName")
    }


    /**
     * 预加载玩家领域
     */
    fun onLoadDataCache() {
        val list = mutableListOf<File>()

        measureTimeMillis {
            list.also {
                it.addAll(CacheDataFile.forFile())
            }
            val ac = arrayOf("world", "world_nether", "world_the_end")
            val event = object : Listener {
                @EventHandler
                fun a (event: WorldInitEvent) {
                    val name = event.world.name
                    GeekWorldField.debug("WorldInitEvent $name")
                    if (!ac.contains(name)) {
                        event.world.keepSpawnInMemory = false
                    }
                }
            }
            Bukkit.getPluginManager().registerEvents(event, GeekWorldField.instance)
            list.forEach { file ->
                InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8).use {
                    BufferedReader(it).use { bf ->
                        val json = Gson().fromJson(bf, CacheDataPack::class.java)
                        if (json.time < (System.currentTimeMillis()+SetTings.pluginCfg.worldLoads)) {
                            GeekWorldField.debug("初始化世界")
                            for (str in json.getWorlds()) {
                                if (str.contains("_world_nether")) {
                                    createWorld(str, FieldWorldType.NETHER_WORLD)
                                    continue
                                }
                                if (str.contains("_world_the_end")) {
                                    createWorld(str, FieldWorldType.THEEND_WORLD)
                                    continue
                                }
                                createWorld(str, FieldWorldType.MAIN_WORLD)
                            }
                        }
                    }
                }
            }
            HandlerList.unregisterAll(event)
        }.also {
            GeekWorldField.debug("§7预加载 &f${list.size} &7个世界领域... §8(耗时 $it Ms)")
        }
    }
    /**
     * 预加载地图缓存
     */
    fun onLoadMapsCache() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.also {
                it.addAll(CacheMapsFile.forFile())
            }
            list.forEach { file ->
                InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8).use {
                    BufferedReader(it).use { bf ->
                         cacheWorld.add(Gson().fromJson(bf, CacheMapsPack::class.java))
                    }
                }
            }
        }.also {
            GeekWorldField.debug("§7已缓存 &f${cacheWorld.size} &7个调度文件... §8(耗时 $it Ms)")
        }
    }


    /**
     * 启动预生产世界
     */
    fun startWorldPre() {
        if (SetTings.preSpawnCfg.isStart) {
            SetTings.preSpawnCfg.isStart = false
            SetTings.config["Pre.isStart"] = false
            SetTings.config.saveToFile()
            TaskHub.worldSaveToFileTask
            WorldSpawnTask(SetTings.preSpawnCfg.number).run()
        }
    }

}
package me.geek.world.common.world



import com.google.gson.annotations.Expose
import me.geek.world.api.data.PlayerData
import me.geek.world.api.data.PlayerManage
import me.geek.world.api.event.FieldWorldCreateEvent
import me.geek.world.api.event.FieldWorldLoadEvent
import me.geek.world.api.flag.Access
import me.geek.world.api.world.FieldWorld
import me.geek.world.api.world.FieldWorldType
import me.geek.world.api.world.WorldManage
import me.geek.world.api.world.WorldManage.signOffline
import me.geek.world.api.world.WorldManage.singOnline
import me.geek.world.common.data.TeamData

import org.bukkit.*
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync

import java.util.*



class FieldWorldImpl(
    override val owner: UUID
) : FieldWorld {

    @Expose
    override var spawnString: String = ""

    @Expose
    override var spawnLoc: Location? = null

    override var team: MutableList<TeamData> = mutableListOf()
        private set

    override var world: String =  "${owner}_world"
        private set
    override var worldNether: String = "${owner}_world_nether"
        private set

    override var worldTheEnd: String = "${owner}_world_the_end"
        private set

    override var access: Access = Access.Public
    override var difficulty: Difficulty = Difficulty.NORMAL
    override val gameMode: GameMode = GameMode.SURVIVAL
    override var border: Int = 100


    /**
     * 后期将通过世界资源调度进行 世界文件获取
     */
    fun createWorld(): FieldWorld {
        submitAsync {
            cacheWorldScheduler()
        }
        return this
    }
    private fun cacheWorldScheduler() {
        WorldManage.getCacheWorld().copyToServer(owner)
        submit {
            if (world.isNotEmpty()) {
                WorldManage.createWorld(world, FieldWorldType.MAIN_WORLD).also {
                    it.difficulty = difficulty
                    it.worldBorder.size = border.toDouble()
                    it.viewDistance = 6
                    // 唤起领域创建事件
                    FieldWorldCreateEvent(owner, world).call()
                    // 唤起领域加载世界
                    FieldWorldLoadEvent(owner, it, true).call()
                }
            }
            if (worldNether.isNotEmpty()) {
                WorldManage.createWorld(worldNether, FieldWorldType.NETHER_WORLD).also {
                    it.difficulty = difficulty
                    it.worldBorder.size = border.toDouble()
                    it.viewDistance = 6
                    // 唤起领域加载世界
                    FieldWorldLoadEvent(owner, it, false).call()
                }
            }
            if (worldTheEnd.isNotEmpty()) {
                WorldManage.createWorld(worldTheEnd, FieldWorldType.THEEND_WORLD).also {
                    it.difficulty = difficulty
                    it.worldBorder.size = border.toDouble()
                    it.viewDistance = 6
                    // 唤起领域加载世界
                    FieldWorldLoadEvent(owner, it, false).call()
                }
            }
            singOnline()
        }
    }


    override fun isFiledWorld(worldName: String): Boolean {
        return worldName == world || worldName == worldNether || worldName == worldTheEnd
    }

    override fun isNotPlayer(): Boolean {
        getMailWorld()?.let {
            if (it.playerCount > 0) return false
        }
        getNetherWorld().let {
            if (it.playerCount > 0) return false
        }
        getTheEndWorld().let {
            if (it.playerCount > 0) return false
        }
        return true
    }

    override fun kickPlayer(uuid: UUID): Boolean {
        if (isNotPlayer()) {
            return false
        } else {
            getMailWorld()?.let {
                for (player in it.players) {
                    if (player.uniqueId == uuid) {
                        // 执行剔除操作
                        return true
                    }
                }
            }
            getNetherWorld().let {
                for (player in it.players) {
                    if (player.uniqueId == uuid) {
                        // 执行剔除操作
                        return true
                    }
                }
            }
            getTheEndWorld().let {
                for (player in it.players) {
                    if (player.uniqueId == uuid) {
                        // 执行剔除操作
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onloadWorld(): Boolean {
        submit {
            if (world.isNotEmpty()) {
                 WorldManage.createWorld(world, FieldWorldType.MAIN_WORLD)
            }
            if (worldNether.isNotEmpty()) {
                WorldManage.createWorld(worldNether, FieldWorldType.NETHER_WORLD)
            }
            if (worldTheEnd.isNotEmpty()) {
                WorldManage.createWorld(worldTheEnd, FieldWorldType.THEEND_WORLD)
            }
            singOnline()
        }
        return true
    }

    override fun unloadWorld(): Boolean {
        submit {
            if (world.isNotEmpty()) {
                Bukkit.unloadWorld(world, true)
            }
            if (worldNether.isNotEmpty()) {
                Bukkit.unloadWorld(worldNether, true)
            }
            if (worldTheEnd.isNotEmpty()) {
                Bukkit.unloadWorld(worldTheEnd, true)
            }
            // 标记离线
            signOffline()
        }
        return true
    }

    override fun getPlayerData(): PlayerData? {
        return PlayerManage.getPlayerData(owner)
    }

    override fun getMailWorld(): World? {
        return Bukkit.getWorld(this.world)
    }

    override fun getNetherWorld(): World {
        return Bukkit.getWorld(this.worldNether) ?: error("世界 $worldNether 不存在，可能未成功加载或已经被删除...")
    }

    override fun getTheEndWorld(): World {
        return Bukkit.getWorld(this.worldTheEnd) ?: error("世界 $worldTheEnd 不存在，可能未成功加载或已经被删除...")
    }

}
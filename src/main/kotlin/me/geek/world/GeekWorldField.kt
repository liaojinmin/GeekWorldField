package me.geek.world

import me.geek.world.api.data.PlayerManage

import me.geek.world.api.world.WorldManage
import me.geek.world.common.hook.HookPlugin
import me.geek.world.common.listener.BasicListener
import me.geek.world.common.listener.WorldListener
import me.geek.world.common.menu.MenuManage
import me.geek.world.settings.SetTings
import me.geek.world.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.BukkitPlugin

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
@RuntimeDependencies(
    RuntimeDependency(value = "!com.zaxxer:HikariCP:4.0.3",
        relocate = ["!com.zaxxer.hikari",
            "!com.zaxxer.hikari_4_0_3_world"]),
    RuntimeDependency(value = "org.xerial.snappy:snappy-java:1.1.8.4",
        transitive = true, ignoreOptional = false
        // repository = "https://repo1.maven.org/maven2",
    ),
)
@PlatformSide([Platform.BUKKIT])
object GeekWorldField: Plugin() {
    val instance by lazy { BukkitPlugin.getInstance() }
    const val VERSION = 1.0
    val BukkitVersion by lazy { Bukkit.getVersion().substringAfter("MC:").filter { it.isDigit() }.toInt() }


    override fun onLoad() {
        console().sendMessage("")
        console().sendMessage("正在加载 §3§lGeekWorldField §f...  §8" + Bukkit.getVersion())
        console().sendMessage("")
    }

    override fun onEnable() {
        console().sendMessage("")
        console().sendMessage("       §aGeekWorldField§8-§6Plus  §bv$VERSION §7by §awww.geekcraft.ink")
        console().sendMessage("       §8适用于Bukkit: §71.12.2-1.19.2 §8当前: §7 ${Bukkit.getServer().version}")
        console().sendMessage("")

        SetTings.onLoadSetTings() // 加载配置文件

        listenerHub() // 事件启动

        PlayerManage.start() // 数据库模块启动

        HookPlugin.onHook() // 挂钩软依赖
    }
    override fun onActive() {

    }

    override fun onDisable() {
        MenuManage.closeGui()
        PlayerManage.closeData()
    }




    @JvmStatic
    fun say(msg: String) {
        if (BukkitVersion >= 1160)
            console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekWorldField&8] &7$msg".colorify())
        else
            console().sendMessage("§8[§6GeekWorldField§8] ${msg.replace("&", "§")}")
    }
    @JvmStatic
    fun debug(msg: String) {
        if(SetTings.DeBug) {
            if (BukkitVersion >= 1160)
                console().sendMessage("&8[<g#2:#FFB5C5:#EE0000>GeekWorldField&8] &cDeBug &8| &7$msg".colorify())
            else
                console().sendMessage("§8[§6GeekWorldField§8] ${msg.replace("&", "§")}")
        }
    }

    /**
     * 承载区启动逻辑
     */
    private fun listenerHub() {
        if (SetTings.pluginCfg.isMailServer && SetTings.pluginCfg.startCluster) return

        WorldManage.startWorldPre() // 启动预生成世界文件
        WorldManage.onLoadDataCache() // 预加载世界
        WorldManage.onLoadMapsCache() // 加载调度文件

        getServer().pluginManager.registerEvents(WorldListener(), this.instance)
        getServer().pluginManager.registerEvents(BasicListener(), this.instance)

    }
}
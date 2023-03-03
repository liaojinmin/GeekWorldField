package me.geek.world.settings

import me.geek.world.GeekWorldField
import me.geek.world.scheduler.sql.SqlConfig
import me.geek.world.utils.Expiry
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.configuration.Configuration.Companion.getObject
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/12/1
 *
 **/
@PlatformSide([Platform.BUKKIT])
object SetTings {

    @Config(value = "settings.yml", autoReload = true)
    lateinit var config: ConfigFile
        private set

    @Awake(LifeCycle.ENABLE)
    fun init() {
        config.onReload { onLoadSetTings() }
    }

    lateinit var sqlConfig: SqlConfig
    lateinit var pluginCfg: PluginCfg
    lateinit var preSpawnCfg: PreSpawnCfg
    var DeBug: Boolean = false


    fun onLoadSetTings() {
        measureTimeMillis {
            DeBug = config.getBoolean("debug", false)
            sqlConfig = config.getObject("data_storage", false)
            sqlConfig.sqlite = GeekWorldField.instance.dataFolder
            pluginCfg = config.getObject("Config", false)
            preSpawnCfg = config.getObject("Pre", false)
        }
    }

}
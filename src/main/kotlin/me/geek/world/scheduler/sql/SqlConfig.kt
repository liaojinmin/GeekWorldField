package me.geek.world.scheduler.sql

import java.io.File

/**
 * 作者: 老廖
 * 时间: 2022/10/16
 *
 **/
data class SqlConfig(
    val use_type: String = "sqlite",
    val mysql: MysqlConfig = MysqlConfig(),
    val hikari_settings: HikariConfig = HikariConfig(),
) {
    var sqlite: File? = null
}

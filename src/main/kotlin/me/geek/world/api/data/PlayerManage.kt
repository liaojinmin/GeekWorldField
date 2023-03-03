package me.geek.world.api.data

import me.geek.world.api.flag.FlagBase
import me.geek.world.common.data.PlayersDataImpl
import me.geek.world.common.world.CacheDataPack
import me.geek.world.scheduler.SQLImpl
import me.geek.world.scheduler.sql.Mysql
import me.geek.world.scheduler.sql.Sqlite
import me.geek.world.scheduler.sql.action
import me.geek.world.scheduler.sql.use
import me.geek.world.settings.SetTings

import org.bukkit.entity.Player
import java.sql.Connection
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
object PlayerManage {

    /**
     * 玩家缓存
     */
    private val PlayerCache: MutableMap<UUID, PlayerData> = ConcurrentHashMap()
    private val SqlImpl: SQLImpl = SQLImpl()

    fun getPlayerCache() : MutableMap<UUID, PlayerData> {
        return PlayerCache
    }

    fun Player.getPlayerData(): PlayerData {
        return PlayerCache[this.uniqueId] ?: SqlImpl.select(this).also {
            PlayerCache[this.uniqueId] = it
        }
    }
    fun getPlayerData(uuid: UUID): PlayerData? {
        return PlayerCache[uuid]
    }

    /**
     * 保存玩家数据，使用数据库，需要异步
     */
    fun Player.savePlayerData() {
        PlayerCache[this.uniqueId]?.let {
            // 保存缓存
            if (it.fieldWorld != null) CacheDataPack(it.uuid, it.user, System.currentTimeMillis()).saveToFile()
            SqlImpl.update(it)
        }
    }

    /**
     * 保存玩家数据，使用数据库，需要异步
     */
    fun PlayerData.savePlayerData() {
        // 保存缓存
        if (this.fieldWorld != null) CacheDataPack(this.uuid, this.user, System.currentTimeMillis()).saveToFile()
        SqlImpl.update(this)
    }

    fun Player.getDefaultPlayerData(set: Boolean = true): PlayerData {
        val data =  PlayersDataImpl(
            this,
            null,
            FlagBase.getDefaultOwnerFlag())
        if (set) PlayerCache[this.uniqueId] = data
        return data
    }




    private val dataSub by lazy {
        if (SetTings.sqlConfig.use_type.equals("mysql", ignoreCase = true)) {
            return@lazy Mysql(SetTings.sqlConfig)
        } else return@lazy Sqlite(SetTings.sqlConfig)
    }
    // 返回数据库状态
    fun isActive(): Boolean = dataSub.isActive

    // 获取数据库连接
    fun getConnection(): Connection {
        return dataSub.getConnection()
    }

    // 关闭数据库
    fun closeData() {
        dataSub.onClose()
    }

    // 启动数据库
    fun start() {
        if (dataSub.isActive) return //避免重复启动
        dataSub.onStart()
        if (dataSub.isActive) {
            dataSub.createTab {
                getConnection().use {
                    createStatement().action { statement ->
                        if (dataSub is Mysql) {
                            statement.addBatch(SqlTab.MYSQL_1.tab)
                        } else {
                            statement.addBatch("PRAGMA foreign_keys = ON;")
                            statement.addBatch("PRAGMA encoding = 'UTF-8';")
                            statement.addBatch(SqlTab.SQLITE_1.tab)
                        }
                        statement.executeBatch()
                    }
                }
            }
        }
    }
    enum class SqlTab(val tab: String) {
        SQLITE_1(
            "CREATE TABLE IF NOT EXISTS `player_data` (" +
                    " `uuid` CHAR(36) NOT NULL UNIQUE PRIMARY KEY, " +
                    " `user` varchar(16) NOT NULL UNIQUE," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL" +
                    ");"
        ),

        MYSQL_1(
            "CREATE TABLE IF NOT EXISTS `player_data` (" +
                    " `uuid` CHAR(36) NOT NULL UNIQUE," +
                    " `user` varchar(16) NOT NULL UNIQUE," +
                    " `data` longblob NOT NULL," +
                    " `time` BIGINT(20) NOT NULL," +
                    "PRIMARY KEY (`uuid`, `user`)" +
                    ");"
        ),
    }


}
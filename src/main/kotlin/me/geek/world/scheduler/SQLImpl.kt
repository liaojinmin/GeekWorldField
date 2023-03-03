package me.geek.world.scheduler

import me.geek.world.api.data.PlayerData
import me.geek.world.api.data.PlayerManage
import me.geek.world.api.data.PlayerManage.getDefaultPlayerData
import me.geek.world.io.toPlayerData
import me.geek.world.scheduler.sql.actions
import me.geek.world.scheduler.sql.use
import org.bukkit.entity.Player

class SQLImpl {
    private val manager by lazy { PlayerManage }

    private fun insert(data: PlayerData) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "INSERT INTO player_data(`uuid`,`user`,`data`,`time`) VALUES(?,?,?,?);"
                ).actions { p ->
                    p.setString(1, data.uuid.toString())
                    p.setString(2, data.user)
                    p.setBytes(3, data.toByteArray())
                    p.setString(4, System.currentTimeMillis().toString())
                    p.execute()
                }
            }
        }
    }
    fun update(data: PlayerData) {
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "UPDATE `player_data` SET `user`=?,`data`=?,`time`=? WHERE `uuid`=?;"
                ).actions { p ->
                    p.setString(1, data.user)
                    p.setBytes(2, data.toByteArray())
                    p.setString(3, System.currentTimeMillis().toString())
                    p.setString(4, data.uuid.toString())
                    p.executeUpdate()
                }
            }
        }
    }
    fun select(player: Player, upEmpty: Boolean = true): PlayerData {
        var data: PlayerData? = null
        if (manager.isActive()) {
            manager.getConnection().use {
                this.prepareStatement(
                    "SELECT `data` FROM `player_data` WHERE uuid=?;"
                ).actions { p ->
                    p.setString(1, player.uniqueId.toString())
                    val res = p.executeQuery()
                    data = if (res.next()) {
                        res.getBytes("data").toPlayerData()
                    } else {
                        player.getDefaultPlayerData() .also {
                            if (upEmpty) insert(it)
                        }
                    }
                }
            }
            return data!!
        } else error("数据库发生异常，请检查sql服务器状态，并报告此问题。。。")
    }
}
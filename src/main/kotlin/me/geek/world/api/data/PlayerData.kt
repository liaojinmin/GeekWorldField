package me.geek.world.api.data



import me.geek.world.api.flag.OwnerFlag
import me.geek.world.api.world.FieldWorld

import org.bukkit.entity.Player
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
interface PlayerData {
    /**
     * 玩家 UUID
     */
    val uuid: UUID
    /**
     * 玩家名称
     */
    val user: String


    /**
     * 下线时间
     */
    var dumpTime: Long


    /**
     * 领域世界
     */
    var fieldWorld: FieldWorld?


    /**
     * 领域 标志
     */
    var flag: OwnerFlag


    /**
     * 根据UUID获取玩家
     */
    fun getPlayer(): Player?


    /**
     * 数据序列化
     */
    fun toByteArray(): ByteArray

    /**
     * 将数据转到 JSON
     */
    fun toJsonText(): String
}
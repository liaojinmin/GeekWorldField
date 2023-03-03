package me.geek.world.api.flag

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
interface Flag {

    /**
     * 玩家破坏方块
     */
    var breaking: Boolean

    /**
     * 玩家放置方块
     */
    var placing: Boolean

    /**
     * 拾取物品
     */
    var pickup: Boolean

    /**
     * 丢弃物品
     */
    var drop: Boolean

    /**
     * 玩家攻击
     */
    var damage: Boolean


    /**
     * 使用桶
     */
    var useBucket: Boolean


    /**
     * 是否允许使用、打开容器
     */
    var useContainer: Boolean

    /**
     * 踢除玩家
     */
    var kick: Boolean

    /**
     * 封禁玩家
     */
    var ban: Boolean


}
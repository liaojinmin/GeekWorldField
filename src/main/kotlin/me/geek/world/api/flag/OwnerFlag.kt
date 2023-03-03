package me.geek.world.api.flag


/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
data class OwnerFlag(
    override var breaking: Boolean,
    override var placing: Boolean,
    override var pickup: Boolean,
    override var drop: Boolean,
    override var damage: Boolean,
    override var useBucket: Boolean,
    override var useContainer: Boolean,
    override var kick: Boolean,
    override var ban: Boolean
): FlagBase()

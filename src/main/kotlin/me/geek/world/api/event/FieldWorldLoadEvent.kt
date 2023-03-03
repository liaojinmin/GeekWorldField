package me.geek.world.api.event

import org.bukkit.World
import taboolib.platform.type.BukkitProxyEvent
import java.util.*

/**
 * 玩家的世界加载时触发
 */
class FieldWorldLoadEvent(val owner: UUID, val world: World, val isMain: Boolean) : BukkitProxyEvent()
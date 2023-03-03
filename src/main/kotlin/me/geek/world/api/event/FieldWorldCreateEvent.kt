package me.geek.world.api.event

import taboolib.platform.type.BukkitProxyEvent
import java.util.*

/**
 * 当玩家创建世界时触发
 */
class FieldWorldCreateEvent(val owner: UUID, val world: String): BukkitProxyEvent()
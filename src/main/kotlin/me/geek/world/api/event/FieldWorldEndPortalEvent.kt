package me.geek.world.api.event

import org.bukkit.World
import org.bukkit.WorldType
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * 玩家在末地进入传送门时触发
 */
class FieldWorldEndPortalEvent(val player: Player, val evn: World.Environment): BukkitProxyEvent()
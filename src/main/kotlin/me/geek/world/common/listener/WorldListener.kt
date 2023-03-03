package me.geek.world.common.listener

import me.geek.world.GeekWorldField

import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.event.FieldWorldCreateEvent
import me.geek.world.api.event.FieldWorldEndPortalEvent
import me.geek.world.api.event.FieldWorldLoadEvent
import me.geek.world.api.flag.Access
import me.geek.world.api.world.WorldManage

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

import org.bukkit.event.entity.EntityPortalEvent


import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.*


class WorldListener: Listener {

    /**
     * 用于在世界加载完成后进行玩家传送
     */
    @EventHandler
    fun onload(event: FieldWorldLoadEvent) {
        GeekWorldField.debug("====FieldWorldLoadEvent====")
        if (event.isMain) Bukkit.getPlayer(event.owner)?.teleport(event.world.spawnLocation)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(event: PlayerRespawnEvent) {
        GeekWorldField.debug("====PlayerRespawnEvent====")
        val player = event.player
        player.getPlayerData().fieldWorld?.let {
        //    GeekWorldField.debug("领域存在，准备传送")
            it.getMailWorld()?.spawnLocation?.let { loc ->
                player.teleport(loc)
           //     GeekWorldField.debug("传送...$loc")
                //event.respawnLocation = loc
            }
           // event.respawnLocation = it.getMailWorld()!!.spawnLocation
        }
    }
    /**
     * 用于玩家首次创建世界时，设置出生点
     */
    @EventHandler
    fun onload(event: FieldWorldCreateEvent) {
        GeekWorldField.debug("====FieldWorldCreateEvent====")
        Bukkit.getPlayer(event.owner)?.let {
            it.getPlayerData().fieldWorld?.let { fieldWorld ->
                it.bedSpawnLocation = fieldWorld.getMailWorld()!!.spawnLocation
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun a(event: PlayerPortalEvent) {
        GeekWorldField.debug("====PlayerPortalEvent====")
        val player = event.player
        player.getPlayerData().fieldWorld?.let {
            when (event.to.world.name) {
                "world" -> {
                    it.getMailWorld()?.let { world ->
                        event.to = world.spawnLocation
                        //GeekWorldField.debug("修改目的地: ${event.to}")
                    }
                    return
                }
                "world_nether" -> {
                    it.getNetherWorld()?.let { world ->
                        event.to = world.spawnLocation
                        //GeekWorldField.debug("修改目的地: ${event.to}")
                    }
                    return
                }
                "world_the_end" -> {
                    it.getTheEndWorld()?.let { world ->
                        event.to = world.spawnLocation
                        //GeekWorldField.debug("修改目的地: ${event.to}")
                    }
                    return
                }

                else -> {}
            }
        }
    }
    @EventHandler
    fun b(event: EntityPortalEvent) {
        GeekWorldField.debug("====EntityPortalEvent====")
        event.to?.let {
            val name = it.world?.name ?: ""
            if (name != "world" && name != "world_nether" && name != "world_the_end") {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun b(event: FieldWorldEndPortalEvent) {
        GeekWorldField.debug("====FieldWorldEndPortalEvent====")
        val player = event.player
        if (event.evn != World.Environment.THE_END) return
        GeekWorldField.debug("尝试返回领域...")
        player.getPlayerData().fieldWorld?.let {
            player.teleport(it.getMailWorld()?.spawnLocation ?: player.bedSpawnLocation!!)
            GeekWorldField.debug("成功返回领域...")
        }
    }

    @EventHandler
    fun onMove(event: PlayerTeleportEvent) {
        GeekWorldField.debug("====PlayerTeleportEvent====")
        when (event.cause) {
            END_PORTAL -> { // 末地传送门
                GeekWorldField.debug("END_PORTAL")
                return
            }
            ENDER_PEARL -> { // 末影珍珠
                GeekWorldField.debug("ENDER_PEARL")
                return
            }
            END_GATEWAY -> { // 末地折跃门
                GeekWorldField.debug("END_GATEWAY")
                return
            }
            NETHER_PORTAL -> { // 地狱传送门
                GeekWorldField.debug("NETHER_PORTAL")
                return
            }
            UNKNOWN -> {
                GeekWorldField.debug("UNKNOWN")
                return
            }
            else -> {
                WorldManage.getFieldWorld(event.to.world.name)?.let {
                    if (it.access == Access.Private) {
                        event.isCancelled = true
                        event.player.sendMessage("这个领域是私有的，你无法进入")
                    }
                }
                GeekWorldField.debug(event.cause.toString())
            }
        }

    }

}
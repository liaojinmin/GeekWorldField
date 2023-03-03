package me.geek.world.common.listener

import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.data.PlayerManage.savePlayerData
import me.geek.world.api.event.FieldWorldEndPortalEvent
import me.geek.world.api.world.WorldManage.singOnline
import me.geek.world.settings.SetTings
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
object PlayerListener {
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onJoin(e: PlayerJoinEvent) {
        GeekWorldField.debug("====PlayerJoinEvent====")
        val player = e.player

        player.getPlayerData().fieldWorld?.let {
            GeekWorldField.debug("玩家世界存在，准备传送...")
            it.getMailWorld()?.spawnLocation?.let { loc ->
                it.singOnline()
                GeekWorldField.debug("传送...")
                player.teleport(loc)
            } ?: it.onloadWorld() // 加载世界
        } ?: GeekWorldField.debug("这个玩家还没有创建领域世界...")
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onQuit(e: PlayerQuitEvent) {
        GeekWorldField.debug("-====PlayerQuitEvent====-")
        val player = e.player
        player.getPlayerData().let {
            GeekWorldField.debug("记录玩家下线时间...")
            it.dumpTime = System.currentTimeMillis() + SetTings.pluginCfg.offLines
            submitAsync { it.savePlayerData() }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onMove(event: PlayerMoveEvent) {

        if (event.to.block.type == Material.END_PORTAL) {
            event.isCancelled = true
            FieldWorldEndPortalEvent(event.player, event.to.world.environment).call()
        }
    }



}
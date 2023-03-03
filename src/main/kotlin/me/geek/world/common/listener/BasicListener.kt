package me.geek.world.common.listener

import me.geek.world.GeekWorldField
import me.geek.world.api.world.WorldManage
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import taboolib.platform.util.isRightClickBlock

class BasicListener : Listener {


    /**
     * 破坏方块
     */
    @EventHandler(ignoreCancelled = true)
    fun a(event: BlockBreakEvent) {
        GeekWorldField.debug("BlockBreakEvent")

        val player = event.player

        val world = event.block.world.name
        // 获取玩家所在领域世界，如果不是领域世界或不在线 则 null
        WorldManage.getFieldWorld(world)?.let {
            GeekWorldField.debug("getFieldWorld != null")
            // 获取这个世界的玩家数据
            it.getPlayerData()?.let { playerData ->

                GeekWorldField.debug("playerData != null")
                // 如果这个世界运行破坏 则终止判断
                if (playerData.flag.breaking || player.uniqueId == playerData.uuid) {
                    return
                } else {
                    // 不允许破坏，继续检查权限
                    if (!WorldManage.hasPerm(player, world)) {
                        event.isCancelled = true
                    }
                }
            } ?: run {
                GeekWorldField.debug("playerData == null")
                // 玩家数据离线，继续检查权限
                if (!WorldManage.hasPerm(player, world)) {
                    event.isCancelled = true
                }
            }

        } ?: return // 如果不是领域世界
    }

    /**
     * 放置方块
     */
    @EventHandler(ignoreCancelled = true)
    fun b(event: BlockPlaceEvent) {
        val player = event.player
        val world = event.block.world.name
        // 如果这个领域允许使用攻击
        WorldManage.getFieldWorld(world)?.let {
            it.getPlayerData()?.let { playerData ->
                if (playerData.flag.placing || player.uniqueId == playerData.uuid) {
                    return
                }
            }
        }?: return // 如果不是领域世界

        if (!WorldManage.hasPerm(player, world)) {
            event.isCancelled = true
        }
    }

    /**
     * 掉落物拾取
     */
    @EventHandler(ignoreCancelled = true)
    fun c(event: EntityPickupItemEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player

            val world = event.entity.world.name

            WorldManage.getFieldWorld(world)?.let {
                it.getPlayerData()?.let { playerData ->
                    if (playerData.flag.pickup  || player.uniqueId == playerData.uuid) {
                        return
                    }
                }
            } ?: return // 如果不是领域世界

            if (!WorldManage.hasPerm(player, world)) {
                event.isCancelled = true
            }
        }
    }
    /**
     * 掉落物拾丢弃
     */
    @EventHandler(ignoreCancelled = true)
    fun d(event: PlayerDropItemEvent) {
        val player = event.player

        val world = player.world.name

        WorldManage.getFieldWorld(world)?.let {
            it.getPlayerData()?.let { playerData ->
                if (playerData.flag.drop || player.uniqueId == playerData.uuid) {
                    return
                }
            }
        } ?: return // 如果不是领域世界

        if (!WorldManage.hasPerm(player, world)) {
            event.isCancelled = true
        }

    }

    /**
     * 攻击生物
     */
    @EventHandler(ignoreCancelled = true)
    fun e(event: EntityDamageByEntityEvent) {  // 存在问题
        if (event.damager is Player || event.damager is Projectile && (event.damager as Projectile).shooter is Player) {
            val player = event.damager as Player
            val world = player.world.name
            WorldManage.getFieldWorld(world)?.let {

                it.getPlayerData()?.let { playerData ->
                    if (playerData.flag.damage || player.uniqueId == playerData.uuid) {
                        return
                    }
                }
            } ?: return // 如果不是领域世界
            if (!WorldManage.hasPerm(player, world)) {
                event.isCancelled = true
            }
        }
    }


    /**
     * 桶的使用
     */
    @EventHandler(ignoreCancelled = true)
    fun f(event: PlayerBucketEmptyEvent) {
        bucket(event.player, event.player.world.name, event)
    }
    /**
     * 桶的使用
     */
    @EventHandler(ignoreCancelled = true)
    fun f(event: PlayerBucketFillEvent) {
        bucket(event.player, event.player.world.name, event)
    }
    /*
    /**
     * 桶的使用
     */
    @EventHandler(ignoreCancelled = true)
    fun f(event: PlayerBucketEntityEvent) {
        bucket(event.player, event.player.world.name, event)
    }

     */

    private fun bucket(player: Player, worldName: String, event: PlayerBucketEvent) {
        WorldManage.getFieldWorld(worldName)?.let {
            it.getPlayerData()?.let { playerData ->
                if (playerData.flag.useBucket || player.uniqueId == playerData.uuid) {
                    return
                }
            }
        } ?: return // 如果不是领域世界

        if (!WorldManage.hasPerm(player, worldName)) {
            event.isCancelled = true
        }
    }



    /**
     * 容器使用
     */
    @EventHandler(ignoreCancelled = true)
    fun g(event: PlayerInteractEvent) {
        val player = event.player
        if (event.isRightClickBlock() && event.clickedBlock != null && event.hand == EquipmentSlot.HAND) {
            val material = event.clickedBlock!!.type
            if (material == Material.CHEST || material == Material.TRAPPED_CHEST
                || material == Material.ENDER_CHEST || material == Material.FURNACE
                || material == Material.DISPENSER || material == Material.DROPPER
                || material == Material.ANVIL || material == Material.BREWING_STAND
                || material == Material.JUKEBOX || material == Material.NOTE_BLOCK
                || material == Material.HOPPER) {

                val world = player.world.name

                WorldManage.getFieldWorld(world)?.let {
                    it.getPlayerData()?.let { playerData ->
                        if (playerData.flag.useContainer || player.uniqueId == playerData.uuid) {
                            return
                        }
                    }
                } ?: return // 如果不是领域世界
                if (!WorldManage.hasPerm(player, world)) {
                    event.isCancelled = true
                }
            }
        }
    }




}
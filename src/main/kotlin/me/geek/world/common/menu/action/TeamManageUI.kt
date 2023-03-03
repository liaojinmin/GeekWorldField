package me.geek.world.common.menu.action

import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerData
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.common.catcher.action.AddTeam
import me.geek.world.common.data.TeamData
import me.geek.world.common.menu.MenuBasic
import me.geek.world.common.menu.MenuData
import me.geek.world.common.menu.MenuManage.openMenu
import me.geek.world.settings.SetTings
import me.geek.world.utils.Heads
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common.platform.function.warning
import taboolib.platform.util.buildItem

class TeamManageUI(
    override val player: Player,
    override val menuData: MenuData,
    private val playerData: PlayerData = player.getPlayerData()
): MenuBasic() {
    private val ioc = mutableMapOf<Int, TeamData>()

    init {
        this.isLock = true
        build()
    }

    override fun build(): MenuBasic {
        var item = this.inventory.contents
        val filed = playerData.fieldWorld?.team ?: emptyList<TeamData>()
        if (filed.isNotEmpty()) {
            var size = filed.size
            while (size > 0) {
                menuData.layout.forEachIndexed { index, value ->

                    if (value != ' ') {
                        menuData.icon[value]?.let { icon ->
                            if (icon.iconType.dis.equals("teams", ignoreCase = true)) {
                                if (size > 0) {
                                    val team = filed[filed.size - size]
                                    val lore = listOf(
                                        "",
                                        "§8| §7UUID: §f${team.teamPlayerUuid}",
                                        "",
                                        "§8| §7状态: ${if (Bukkit.getPlayer(team.teamPlayerUuid) != null) "§a在线" else "§c离线"}",
                                        "",
                                        "§8[§aShift_左键§8] §7- §F从成员中移除玩家",
                                        ""
                                    )
                                    val items = Heads.getHead(team.teamPlayerName)
                                    val meta = items.itemMeta.also {
                                        it.setDisplayName("§7玩家: §f${team.teamPlayerName}")
                                        it.lore = lore
                                    }
                                    items.itemMeta = meta
                                    ioc[index] = team
                                    item[index] = items
                                    size--
                                }
                            }
                        }
                    }
                }
                this.contents.add(item)
                item = this.inventory.contents
            }
        }
        this.openMenu()
        return this
    }

    private var cd: Long = 0
    override fun onClick(event: InventoryClickEvent) {
        event.isCancelled = true
        if (cd < System.currentTimeMillis()) cd = System.currentTimeMillis() + 200 else return
        menuData.layout[event.rawSlot].let {
            menuData.icon[it]?.let { icon ->
                icon.executeCmd(player)
                icon.playSound(player)
                if (icon.iconType.dis.equals("teams", ignoreCase = true)) {
                    val team = ioc[event.rawSlot] ?: return
                    // Shift Click remove team
                    if (event.isLeftClick && event.isShiftClick) {
                        Bukkit.getPlayer(team.teamPlayerUuid)?.let { player2 ->

                            if (playerData.fieldWorld!!.isFiledWorld(player2.world.name)) {

                                if (SetTings.pluginCfg.startCluster && !SetTings.pluginCfg.isMailServer) {
                                    // 传送至集群主服
                                    warning("传送至集群主服")
                                } else {
                                    Bukkit.getWorld("world")?.let { world ->
                                        player2.teleport(world.spawnLocation)
                                        player2.sendMessage("你已被领域主移除成员资格。")
                                    } ?: player2.kickPlayer("由于未找到承载大厅，但你已被领域主剔除，故将您踢出游戏，请重新进入。")
                                    player.sendMessage("成功移除成员: ${player2.name}")
                                }
                            }
                        }
                        playerData.fieldWorld!!.team.removeIf { a -> a == team }
                        ioc.remove(event.rawSlot)
                        this.inventory.setItem(event.rawSlot, null)
                    }
                    return
                }
                if (icon.iconType.dis.equals("add", ignoreCase = true)) {
                    player.closeInventory()
                    AddTeam(player, this).start()
                    return
                }
                if (icon.iconType.dis.equals("back", ignoreCase = true)) {
                    player.openMenu("manage")
                }
            }
        }
    }



    override fun onClose(event: InventoryCloseEvent) {
    }
}
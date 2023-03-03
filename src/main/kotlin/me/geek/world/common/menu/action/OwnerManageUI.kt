package me.geek.world.common.menu.action

import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerData
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.data.PlayerManage.savePlayerData
import me.geek.world.api.flag.Access
import me.geek.world.common.menu.MenuBasic
import me.geek.world.common.menu.MenuData
import me.geek.world.common.menu.MenuManage.openMenu
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import taboolib.common.platform.function.submitAsync

class OwnerManageUI(
    override val player: Player,
    override val menuData: MenuData,
    private val playerData: PlayerData = player.getPlayerData()
): MenuBasic() {
    init {
        this.openMenu()
        build()
        this.isLock = true
    }

    override fun build(): MenuBasic {
        menuData.layout.forEachIndexed { index, c ->
            if (c != ' ') {
                menuData.icon[c]?.let {
                    val item = this.inventory.contents[index]
                    item.lore?.let { lore ->
                      //  GeekWorldField.debug("item.lore != null")
                        item.lore = playerData.flag.parseFlag(playerData, lore)
                    }
                    this.inventory.contents[index] = item
                }
            }
        }
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


                val flag = icon.iconType.dis
                GeekWorldField.debug(flag)
                if (flag == "") return
                if (playerData.flag.isFlag(flag)) {
                    // 设置flag
                    playerData.flag.setFlag(flag, !playerData.flag.getFlag(flag))
                } else {
                    if (flag == "access") {
                        playerData.fieldWorld?.let { filed ->
                            if (filed.access == Access.Private) {
                                filed.access = Access.Public
                            } else {
                                filed.access = Access.Private
                            }
                        }
                    }
                    if (flag == "team") {
                        player.closeInventory()
                        player.openMenu("team")
                        return
                    }
                }

                // 更新显示图标
                val item = event.currentItem!!
                item.lore = playerData.flag.parseFlag(playerData, icon.lore)
                this.inventory.setItem(event.rawSlot, item)
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
      //  submitAsync { playerData.savePlayerData() }
    }

}
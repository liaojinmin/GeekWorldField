package me.geek.world.common.menu.action

import me.geek.world.api.data.PlayerData
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.data.PlayerManage.savePlayerData
import me.geek.world.common.menu.MenuBasic
import me.geek.world.common.menu.MenuData
import me.geek.world.common.world.FieldWorldImpl
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.jetbrains.annotations.NotNull
import taboolib.common.platform.function.submitAsync

class CreateWorldUI(
    override val player: Player,
    override val menuData: MenuData
): MenuBasic() {
    private val playerData: PlayerData = player.getPlayerData()

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
                        item.lore = parseInfo(lore)
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

                if (icon.iconType.dis == "CreateMain") {


                    if (playerData.fieldWorld != null) {
                        player.sendMessage("你已经创建过领域")
                    } else {
                        player.closeInventory()
                        val fieldWorld = FieldWorldImpl(playerData.uuid).apply {
                            createWorld()
                        }
                        playerData.fieldWorld = fieldWorld
                        player.sendMessage("领域创建完成...")
                        submitAsync { player.savePlayerData() }
                    }
                }
            }
        }
    }

    override fun onClose(event: InventoryCloseEvent) {
    }

    private fun parseInfo(@NotNull lore: List<String>): List<String> {
        val list = mutableListOf<String>()
        lore.forEach {
            when {
                it.contains("{state}") -> list.add(it.replace("{state}", if (playerData.fieldWorld != null) "§a已创建" else "未创建"))
                else -> list.add(it)
            }
        }
        return list
    }
}
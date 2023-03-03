package me.geek.world.common.menu


import me.geek.world.GeekWorldField
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

import taboolib.library.xseries.XSound

/**
 * 作者: 老廖
 * 时间: 2023/1/19
 *
 **/
abstract class MenuBasic: MenuHeader {

    abstract fun build(): MenuBasic

    override var title: String = "§6§lGeekWorldField"
    override var size: Int = 54

    /**
     * 锁定所有槽位
     */
    override var isLock: Boolean = true


    val contents: MutableList<Array<ItemStack>> = ArrayList()


    open val inventory: Inventory by lazy {
        this.menuData?.let {
            Bukkit.createInventory(this.player, it.size, it.title).apply {
                if (it.items.isNotEmpty()) {
                    this.contents = it.items
                }
            }
        } ?: Bukkit.createInventory(this.player, size, title)
    }

    /**
     * 打开菜单
     */
    override fun openMenu() {
        this.player.openInventory(this.inventory)
        MenuManage.SessionCache[this.player.uniqueId] = this
        MenuManage.isOpen.add(this.player.uniqueId)
        if (contents.size != 0) {
            this.inventory.contents = this.contents[0]
        }
    }




}
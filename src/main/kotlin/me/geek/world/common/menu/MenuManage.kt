package me.geek.world.common.menu



import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.common.hook.HookPlugin
import me.geek.world.common.menu.action.CreateWorldUI
import me.geek.world.common.menu.action.OwnerManageUI
import me.geek.world.common.menu.action.TeamManageUI
import me.geek.world.common.menu.icon.Icon
import me.geek.world.io.forFile
import me.geek.world.utils.colorify
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.SecuredFile
import taboolib.module.configuration.util.getMap
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * 作者: 老廖
 * 时间: 2022/12/11
 * 此 MENU 事件处理，借鉴 TabooLib
 **/
object MenuManage {

    internal val isOpen: MutableList<UUID> = mutableListOf()
    internal val SessionCache: MutableMap<UUID, MenuBasic> = mutableMapOf()

    private val MenuCache: MutableMap<String, MenuData> = HashMap()

    private val AIR = ItemStack(Material.AIR)


    /**
     * @param name create = 世界创建菜单， 以文件名为准
     */
    fun Player.openMenu(name: String): Boolean {
        val menu = MenuCache[name] ?: return false
        playSound(location, Sound.UI_BUTTON_CLICK, 1f, 2f)
        when (name) {
            "create" -> {
                CreateWorldUI(this, menu)
                return true
            }
            "manage" -> {
                if (this.getPlayerData().fieldWorld == null) return false
                if (this.getPlayerData().fieldWorld!!.isFiledWorld(this.world.name)) {
                    OwnerManageUI(this, menu)
                } else this.sendMessage("管理菜单只能在领域打开")
                return true
            }
            "team" -> {
                if (this.getPlayerData().fieldWorld == null) return false
                TeamManageUI(this, menu)
                GeekWorldField.debug("team open")
                return true
            }
            else -> return false
        }
    }

    fun loadMenu() {
        val list = mutableListOf<File>()
        measureTimeMillis {
            list.also {
                it.addAll(saveDefaultMenu.forFile())
            }
            list.forEach { file ->
                val icon = mutableListOf<Icon>()

                val menu: SecuredFile = SecuredFile.loadConfiguration(file)

                val menuTag: String = file.name.substring(0, file.name.indexOf("."))

                val title: String = menu.getString("title")!!.colorify()

                val size: Int = menu.getStringList("layout").size * 9
                val layout: MutableList<Char> = mutableListOf<Char>().apply {
                    menu.getStringList("layout").forEach {
                        it.indices.forEach { index -> add(it[index]) }
                    }
                }
                menu.getMap<String, ConfigurationSection>("Icons").forEach { (name, obj) ->
                    icon.add(Icon(name[0], obj))
                }
                val items = arrayListOf<ItemStack>()

                val listIcon: MutableMap<Char, Icon> = mutableMapOf<Char, Icon>().apply {
                    layout.forEachIndexed { _, value ->
                        if (value != ' ') {
                            var ok = false
                            icon.forEach { ic ->
                                if (ic.icon == value) {
                                    ok = true
                                    items.add(buildItems(ic))
                                    this[value] = ic
                                }
                            }
                            if (!ok) items.add(AIR)
                        } else items.add(AIR)
                    }
                }
                MenuCache[menuTag] = MenuData(menuTag, title, layout, size, listIcon, items.toTypedArray())
            }
        }.also {
            GeekWorldField.say("§7菜单界面加载完成... §8(耗时 $it ms)");
        }
    }

    fun closeGui() {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            if (isOpen.contains(player.uniqueId)) {
                player.closeInventory()
            }
        }
    }


    private val IA = Regex("(IA|ia|ItemsAdder):")
    private val MM = Regex("(MM|mm|MythicMobs):")
    private fun buildItems(icon: Icon): ItemStack {
        if (icon.icon == ' ' || icon.iconType.dis == "teams") return AIR
        return when {
            icon.mats.contains(IA) -> {
                if (HookPlugin.itemsAdder.isHook) {
                    val meta = icon.mats.split(":")
                    HookPlugin.itemsAdder.getItem(meta[1])
                } else ItemStack(Material.STONE, 1)
            }
            icon.mats.contains(MM) -> TODO()
            else -> {
                val itemStack = try {
                    ItemStack(Material.valueOf(icon.mats.uppercase()), 1, icon.data.toShort())
                } catch (ing: IllegalArgumentException) {
                    ItemStack(Material.STONE, 1)
                }
                val itemMeta = itemStack.itemMeta
                if (itemMeta != null) {
                    itemMeta.setDisplayName(icon.name.colorify())
                    if (icon.lore.size == 1 && icon.lore[0].isEmpty()) {
                        itemMeta.lore = null
                    } else {
                        itemMeta.lore = icon.lore
                    }
                    itemStack.itemMeta = itemMeta
                }
                itemStack
            }
        }
    }

    private val saveDefaultMenu by lazy {
        val dir = File(GeekWorldField.instance.dataFolder, "menu")
        if (!dir.exists()) {
            arrayOf(
                "menu/create.yml",
                "menu/manage.yml",
            ).forEach { releaseResourceFile(it, true) }
        }
        dir
    }




    // 事件管理
    @SubscribeEvent
    fun onOpen(e: InventoryOpenEvent) {
        if (SessionCache[e.view.player.uniqueId] != null) {
            GeekWorldField.debug("非法开启UI")
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun onClick(e: InventoryClickEvent) {
        val menu = SessionCache[e.view.player.uniqueId] ?: return

        if (e.rawSlot < 0 || e.rawSlot >= menu.inventory.size) {
            if (menu.isLock) {
                e.isCancelled = true
            }
            return
        }

        // 锁定主手
        if (e.rawSlot - e.inventory.size - 27 == e.whoClicked.inventory.heldItemSlot
            || e.click == ClickType.NUMBER_KEY
            && e.hotbarButton == e.whoClicked.inventory.heldItemSlot) {
            e.isCancelled = true
        }
        menu.onClick(e)
    }

    @SubscribeEvent
    fun onClose(e: InventoryCloseEvent) {
        val menu = SessionCache[e.view.player.uniqueId] ?: return
        menu.onClose(e)
        SessionCache.remove(e.player.uniqueId)
        isOpen.remove(e.player.uniqueId)

    }
    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onQuit(e: PlayerQuitEvent) {
        SessionCache.remove(e.player.uniqueId)
        isOpen.remove(e.player.uniqueId)
    }


}
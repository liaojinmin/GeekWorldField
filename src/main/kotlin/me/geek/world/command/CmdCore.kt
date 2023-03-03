package me.geek.world.command


import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.command.player.CmdMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.chat.TellrawJson


@CommandHeader(name = "GeekWorldField", aliases = ["world", "gwf"], permissionDefault = PermissionDefault.TRUE )
object CmdCore {




    @CommandBody
    val main = mainCommand {
        execute { sender, _, _ ->
            createHelp(sender)
        }
    }
    @CommandBody
    val home = subCommand {
        execute<Player> { sender, _, _ ->
            GeekWorldField.debug("正在尝试返回领域世界")
            sender.getPlayerData().let {
                it.fieldWorld?.let { world ->
                    world.getMailWorld()?.spawnLocation?.let { loc ->
                        sender.teleport(loc)
                    } ?: sender.sendMessage("错误: 未找到出生点")
                } ?: sender.sendMessage("你没有创建过世界")
            }
        }
    }

    @CommandBody
    val menu = CmdMenu.command




    private fun createHelp(sender: CommandSender) {
        val s = adaptCommandSender(sender)
        s.sendMessage("")
        TellrawJson()
            .append("  ").append("§f§lGeekWorldField§8-§6Pro")
            .hoverText("§7现代化高级独立世界(领域)插件 By GeekCraft.ink")
            .append(" ").append("§f${GeekWorldField.VERSION}")
            .hoverText("""
                §7插件版本: §f${GeekWorldField.VERSION}
            """.trimIndent()).sendTo(s)
        s.sendMessage("")
        s.sendMessage("  §7指令: §f/gwf§8[...]")
        if (sender.hasPermission("GeekWorldField.command.admin")) {
         //   s.sendLang("CMD-HELP-ADMIN")
        }
      //  s.sendLang("CMD-HELP-PLAYER")
    }
}
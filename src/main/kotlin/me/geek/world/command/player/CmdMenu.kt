package me.geek.world.command.player

import me.geek.world.command.CmdExp
import me.geek.world.common.menu.MenuManage.openMenu
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand

object CmdMenu: CmdExp {
    override val command = subCommand {
        dynamic("菜单名称") {
            suggestion<CommandSender>(uncheck = false) { _, _ ->
                listOf("create", "manage")
            }
            execute<Player> { sender, context, _ ->
                sender.openMenu(context.get(1))
            }
        }
    }
}
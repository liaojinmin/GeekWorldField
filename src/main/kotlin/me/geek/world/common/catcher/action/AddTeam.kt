package me.geek.world.common.catcher.action



import me.geek.world.api.data.PlayerManage.getPlayerData
import me.geek.world.api.data.PlayerManage.savePlayerData
import me.geek.world.common.catcher.ChatCatCher
import me.geek.world.common.data.TeamData
import me.geek.world.common.menu.MenuBasic
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit


/**
 * 作者: 老廖
 * 时间: 2022/9/11
 *
 **/
class AddTeam(
    override val player: Player,
    private val menu: MenuBasic
): ChatCatCher() {

    override fun remove() {
        submit {
           // menu.isUnReg = true
            menu.openMenu()
        }
    }
    override fun action(msg: String) {
        Bukkit.getPlayer(msg)?.let {
            if (!player.name.equals(msg, true)) {
                player.getPlayerData().fieldWorld!!.team.add(TeamData(it.uniqueId, it.name))
                submit {
                    //   menu.isUnReg = true
                    menu.openMenu()
                }
                player.savePlayerData()

                player.sendMessage("添加成功")
            } else player.sendMessage("你不能添加你自己")
        } ?: player.sendMessage("玩家不在线或不在该承载区")
    }
    override fun start() {
        super.start()
        player.sendMessage("请输入要添加的玩家")
    }
}
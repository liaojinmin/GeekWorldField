package me.geek.world.api.world

import me.geek.world.api.data.PlayerData
import me.geek.world.api.flag.Access
import me.geek.world.common.data.TeamData
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*


interface FieldWorld {


    /**
     * 主人UUID
     */
    val owner: UUID

    /**
     *  团队成员 - 拥有者不包含在内
     */
    val team: MutableList<TeamData>

    /**
     * 序列化数据
     */
    var spawnString: String

    /**
     * 重生的
     */
    var spawnLoc: Location?

    /**
     * 领域状态 公开、私有
     */
    var access: Access

    /**
     * 玩家 主世界 名称
     */
    val world: String

    /**
     * 玩家 地域世界 名称
     */
    val worldNether: String

    /**
     * 玩家 末地世界 名称
     */
    val worldTheEnd: String

    /**
     * 难度
     */
    var difficulty: Difficulty

    /**
     * 游戏模式
     */
    val gameMode: GameMode

    /**
     * 世界边界
     */
    var border: Int

    /**
     * @param worldName 需要判断的世界名称
     * @return 如果这个世界是这个玩家的，则 true
     */
    fun isFiledWorld(worldName: String): Boolean

    /**
     * 如果这个领域世界有这个玩家则剔除
     * @param uuid 目标玩家UID
     * @return 如果成功剔除，则 true
     */
    fun kickPlayer(uuid: UUID): Boolean

    /**
     * 领域世界是否有玩家
     * 该领域所有世界
     */
    fun isNotPlayer(): Boolean

    /**
     * 将领域卸载
     */
    fun unloadWorld(): Boolean

    /**
     * 将领域世界加载至服务端
     */
    fun onloadWorld(): Boolean

    /**
     * 获取玩家数据
     */
    fun getPlayerData(): PlayerData?

    /**
     * 获取主世界，这个世界可能未加载，需要 null 判断
     */
    fun getMailWorld(): World?

    fun getNetherWorld(): World?

    fun getTheEndWorld(): World?
}
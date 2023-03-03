package me.geek.world.common.data

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import me.geek.world.GeekWorldField
import me.geek.world.api.data.PlayerData
import me.geek.world.api.flag.Access
import me.geek.world.api.flag.Flag
import me.geek.world.api.flag.OwnerFlag
import me.geek.world.api.world.FieldWorld
import me.geek.world.io.Exclude
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.xerial.snappy.Snappy
import java.util.*

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
data class PlayersDataImpl(
    @Expose
    private val player: Player,

    override var fieldWorld: FieldWorld?,

    override var flag: OwnerFlag
) : PlayerData {

    override val uuid: UUID = player.uniqueId

    override val user: String = player.name

    override var dumpTime: Long = 0L



    override fun getPlayer(): Player? {
        return Bukkit.getPlayer(this.uuid)
    }

    override fun toByteArray(): ByteArray {
        val data = this.toJsonText()
        GeekWorldField.debug(data)
        return Snappy.compress(data.toByteArray(charset = Charsets.UTF_8))
    }

    override fun toJsonText(): String {
        /*
        this.fieldWorld?.let {
            var world = ""
            var x = 0.0
            var y = 0.0
            var z = 0.0
            var pitch = 0.0f
            var yaw = 0.0f
            it.spawnLoc?.let { loc ->
                world = loc.world.name
                x = loc.x
                y = loc.y
                z = loc.z
                pitch = loc.pitch
                yaw = loc.yaw
            } ?: run {
                val loc = it.getMailWorld()!!.spawnLocation
                world = loc.world.name
                x = loc.x
                y = loc.y
                z = loc.z
                pitch = loc.pitch
                yaw = loc.yaw
            }
            it.spawnString = "$world;$x;$y;$z;$pitch;$yaw"
        }

         */
        return GsonBuilder()
            .setExclusionStrategies(Exclude())
            .setPrettyPrinting()
            .create()
            .toJson(this)
    }

}


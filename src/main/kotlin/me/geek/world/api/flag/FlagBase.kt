package me.geek.world.api.flag

import me.geek.world.api.data.PlayerData
import org.jetbrains.annotations.NotNull
import taboolib.platform.compat.replacePlaceholder

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
abstract class FlagBase : Flag {

    fun isFlag(dis: String): Boolean {
        return flagList.contains(dis)
    }

    fun setFlag(dis: String, value: Boolean) {
        when (dis) {
            "breaking" -> this.breaking = value
            "placing" -> this.placing = value
            "pickup" -> this.pickup = value
            "drop" -> this.drop = value
            "damage" -> this.damage = value
            "useBucket" -> this.useBucket = value
            "useContainer" -> this.useContainer = value
            else -> error("不存在这个dis： $dis")
        }
    }
    fun getFlag(dis: String): Boolean {
       return when (dis) {
            "breaking" -> this.breaking
            "placing" -> this.placing
            "pickup" -> this.pickup
            "drop" -> this.drop
            "damage" -> this.damage
            "useBucket" -> this.useBucket
            "useContainer" -> this.useContainer
            else -> error("不存在这个dis： $dis")
        }
    }

    fun parseFlag(@NotNull data: PlayerData, @NotNull lore: List<String>): List<String> {
        val list = mutableListOf<String>()
        lore.forEach {
            when (val find = regex.find(it)?.value ?: "") {
                "{breaking}" -> list.add(it.replace(find, data.flag.breaking.toString()))
                "{placing}" -> list.add(it.replace(find, data.flag.placing.toString()))
                "{pickup}" -> list.add(it.replace(find, data.flag.pickup.toString()))
                "{drop}" -> list.add(it.replace(find, data.flag.drop.toString()))
                "{damage}" -> list.add(it.replace(find, data.flag.damage.toString()))
                "{useBucket}" -> list.add(it.replace(find, data.flag.useBucket.toString()))
                "{useContainer}" -> list.add(it.replace(find, data.flag.useContainer.toString()))
                "{owner}" -> list.add(it.replace(find, data.user))
                "{access}" -> list.add(it.replace(find, data.fieldWorld!!.access.dis))
                "{team}" -> list.add(it.replace(find, data.fieldWorld?.team?.map { map -> map.teamPlayerName }?.joinToString() ?: ""))
                else -> {
                    data.getPlayer()?.let { player ->
                        list.add(it.replacePlaceholder(player))
                    } ?: list.add(it)
                }
            }
        }
        return list
    }




    companion object {
        private val regex = Regex("(?<!\\{)\\{(?:([^{}]+)|\\{([^{}]+)})}(?!})")
        private val flagList = mutableListOf<String>().apply {
            add("breaking")
            add("placing")
            add("pickup")
            add("drop")
            add("damage")
            add("useBucket")
            add("useContainer")
        }

        fun getDefaultOwnerFlag(): OwnerFlag {
            return OwnerFlag(
                breaking = false,
                placing = false,
                pickup = false,
                drop = false,
                damage = false,
                useBucket = false,
                useContainer = false,
                kick = true,
                ban = true
            )
        }
    }

}
package me.geek.world.io

import com.google.gson.*
import com.google.gson.annotations.Expose
import me.geek.world.api.data.PlayerData
import me.geek.world.api.flag.Access

import me.geek.world.api.world.FieldWorld
import me.geek.world.common.data.PlayersDataImpl
import me.geek.world.common.data.TeamData
import me.geek.world.common.world.FieldWorldImpl
import org.bukkit.Difficulty
import org.bukkit.GameMode
import org.bukkit.Location
import org.xerial.snappy.Snappy
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.lang.reflect.Type
import java.util.*

class Exclude : ExclusionStrategy {
    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(Expose::class.java) != null
    }

    override fun shouldSkipClass(clazz: Class<*>): Boolean {
        return clazz.getAnnotation(Expose::class.java) != null
    }
}

fun ByteArray.toPlayerData(): PlayerData {
    val gson = GsonBuilder()
        .setExclusionStrategies(Exclude())
        // 注册解析 FieldWorld类 方法
        .registerTypeAdapter(FieldWorld::class.java, UnSerializeFieldWorld())
    return gson.create().fromJson(String(Snappy.uncompress(this), charset = Charsets.UTF_8), PlayersDataImpl::class.java)
}
class UnSerializeFieldWorld: JsonDeserializer<FieldWorld> {
    override fun deserialize(json: JsonElement, p1: Type?, p2: JsonDeserializationContext?): FieldWorld {
        val jsonObject = json.asJsonObject
        val data = setFields(FieldWorldImpl::class.java.invokeConstructor(UUID.fromString(jsonObject.get("owner").asString)),
            "access" to Access.valueOf(jsonObject.get("access").asString),
            "difficulty" to Difficulty.valueOf(jsonObject.get("difficulty").asString),
            "gameMode" to GameMode.valueOf(jsonObject.get("gameMode").asString),
            "border" to jsonObject.get("border").asInt
        ) as FieldWorldImpl


        // 处理团队成员
        jsonObject.get("team")?.asJsonArray?.let {
            it.forEach { vars ->
                val a = vars.asJsonObject
                if (a["teamPlayerUuid"] != null && a["teamPlayerName"] != null) {
                    data.team.add(TeamData(UUID.fromString(a["teamPlayerUuid"].asString), a["teamPlayerName"].asString))
                }
            }
         //  data.team.addAll(Gson().fromJson(it, ArrayList<TeamData>()::class.java))
        }
        return data
    }
}



private fun setFields(any: Any, vararg fields: Pair<String, Any?>): Any {
    fields.forEach { (key, value) ->
        if (value != null) {
            any.setProperty(key, value)
        }
    }
    return any
}
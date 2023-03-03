package me.geek.world.common.world

import com.google.gson.GsonBuilder
import me.geek.world.api.world.WorldManage
import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.UUID

data class CacheDataPack(
    val owner: UUID,
    val user: String,
    val time: Long,
) {
    fun getWorlds(): Array<String> {
        return arrayOf("${owner}_world", "${owner}_world_nether", "${owner}_world_the_end")
    }
    fun saveToFile() {
        val to = File(WorldManage.CacheDataFile, "$owner.json")
        OutputStreamWriter(Files.newOutputStream(to.toPath()), StandardCharsets.UTF_8).use {
            it.write(toJSon())
        }
    }
    private fun toJSon(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }
}

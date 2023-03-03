package me.geek.world.common.world

import cn.hutool.core.io.FileUtil
import com.google.gson.GsonBuilder
import me.geek.world.api.world.WorldManage


import java.io.File
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*

/**
 * 预生成数据缓存
 */
data class CacheMapsPack(
    val indexUUID: UUID,
    val spawnTime: Long
) {
    fun saveToFile(to: File) {
        if (to.exists()) return
        OutputStreamWriter(Files.newOutputStream(to.toPath()), StandardCharsets.UTF_8).use {
            it.write(toJSon())
        }
    }
    fun copyToServer(owner: UUID) {
        // 获得预生成世界文件夹
        val dir = File(WorldManage.CacheMapsFile, indexUUID.toString())
        val sty = File(System.getProperty("user.dir"))
        dir.copyRecursively(sty)
        File(sty, "PreData.json").delete()

        // 重命名
        File(sty, "${indexUUID}_world").renameTo(File(sty, "${owner}_world"))
        File(sty, "${indexUUID}_world_nether").renameTo(File(sty, "${owner}_world_nether"))
        File(sty, "${indexUUID}_world_the_end").renameTo(File(sty, "${owner}_world_the_end"))
    }
    private fun toJSon(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(this)
    }

}


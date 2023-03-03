package me.geek.world.scheduler.task

import me.geek.world.GeekWorldField

import me.geek.world.api.world.FieldWorldType
import me.geek.world.api.world.WorldManage
import me.geek.world.common.world.MovePack
import me.geek.world.scheduler.TaskHub
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import java.io.File
import java.util.UUID
import kotlin.system.measureTimeMillis

/**
 * 启动预生成task
 * @param spawnAmt 需要生成的世界数量
 */
class WorldSpawnTask(private var spawnAmt: Int) {
    /**
     * 是否停止生成
     */
    private var isRun = true


    fun run() {
        measureTimeMillis {
            var amt = spawnAmt
            while (isRun && amt > 0) {

                val uuid = UUID.randomUUID() // 随机生成一个uid
                val a = createWorld("${uuid}_world", FieldWorldType.MAIN_WORLD)
                val b = createWorld("${uuid}_world_nether", FieldWorldType.NETHER_WORLD)
                val c = createWorld("${uuid}_world_the_end", FieldWorldType.THEEND_WORLD)
                val a2 = a.worldFolder
                val b2 = b.worldFolder
                val c2 = c.worldFolder
                Bukkit.unloadWorld(a, true)
                Bukkit.unloadWorld(b, true)
                Bukkit.unloadWorld(c, true)
                val toFile = File(WorldManage.CacheMapsFile, uuid.toString())
                listOf(a2, b2, c2).forEach {
                    TaskHub.worldSaveToFileTask.addTaskPack(MovePack(it, toFile, true, uuid))
                }
                amt--
            }
        }.also {
            GeekWorldField.debug("§7预生成 &f${spawnAmt*3} &7个世界领域... §8(耗时 $it Ms)")
        }
    }

    private fun createWorld(worldName: String, type: FieldWorldType): World {
        return WorldCreator(worldName)
            .type(WorldType.NORMAL)
            .environment(type.evn)
            .createWorld()?.also {
                if (type == FieldWorldType.THEEND_WORLD) {
                    it.spawnLocation = it.spawnLocation.add(10.0, 2.5, 10.0)
                }
            } ?: error("世界创建错误: UID $worldName")
    }



    fun setRun (action: Boolean) {
        this.isRun = action
    }
}
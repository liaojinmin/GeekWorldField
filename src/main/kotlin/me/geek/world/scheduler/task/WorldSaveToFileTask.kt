package me.geek.world.scheduler.task

import me.geek.world.GeekWorldField
import me.geek.world.common.world.CacheMapsPack
import me.geek.world.common.world.MovePack
import taboolib.common.platform.function.submitAsync
import java.io.File

class WorldSaveToFileTask {

    private val taskPack = mutableListOf<MovePack>()

    fun addTaskPack(pack: MovePack) {
        taskPack.add(pack)
    }

    init {
        submitAsync(delay = 5 * 20, period = 5 * 20) {
            if (taskPack.isNotEmpty()) {
                val its = taskPack.iterator()
                while (its.hasNext()) {
                    val data = its.next()
                    val var10 = data.source
                    GeekWorldField.debug("开始复制: ${data.source}")

                    var10.copyRecursively(File(data.target, var10.name), true)
                    if (data.isCache && data.uuid != null) {
                        CacheMapsPack(data.uuid, System.currentTimeMillis()).saveToFile(File(data.target, "PreData.json"))
                    }
                    var10.deleteRecursively()
                    its.remove()
                }
            }
        }
    }
}
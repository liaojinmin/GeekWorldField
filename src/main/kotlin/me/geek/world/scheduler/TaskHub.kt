package me.geek.world.scheduler

import me.geek.world.scheduler.task.WorldSaveToFileTask

object TaskHub {

    val worldSaveToFileTask: WorldSaveToFileTask by lazy { WorldSaveToFileTask() }
}
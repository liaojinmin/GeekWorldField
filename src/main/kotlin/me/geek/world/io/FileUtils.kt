package me.geek.world.io

import java.io.File

/**
 * 作者: 老廖
 * 时间: 2022/11/21
 *
 **/
fun File.forFile(): List<File> {
    return mutableListOf<File>().run {
        if (this@forFile.isDirectory) {
            this@forFile.listFiles()?.forEach {
                addAll(it.forFile())
            }
        } else if (this@forFile.exists() && (this@forFile.absolutePath.endsWith(".yml") || this@forFile.absolutePath.endsWith(".json"))) {
            add(this@forFile)
        }
        this
    }
}
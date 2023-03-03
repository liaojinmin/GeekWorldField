package me.geek.world.common.world

import java.io.File
import java.util.UUID

data class MovePack(val source: File, val target: File, val isCache: Boolean = false, val uuid: UUID? = null)

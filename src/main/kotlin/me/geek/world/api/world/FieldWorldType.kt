package me.geek.world.api.world

import org.bukkit.World

enum class FieldWorldType(val dis: String, val evn: World.Environment) {

    MAIN_WORLD("主世界", World.Environment.NORMAL),

    NETHER_WORLD("下界世界",  World.Environment.NETHER),

    THEEND_WORLD("末地世界",  World.Environment.THE_END)
}


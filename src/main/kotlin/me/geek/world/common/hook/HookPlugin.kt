package me.geek.world.common.hook



import me.geek.world.common.hook.impl.ItemsAdder
import me.geek.world.common.hook.impl.Money
import me.geek.world.common.hook.impl.Points

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 */
object HookPlugin {

    val money  by lazy { Money() }
    val points by lazy { Points() }
    val itemsAdder by lazy { ItemsAdder() }
//    val mythicMobs by lazy { MythicMobs() }



    fun onHook() {
        money
        points
        itemsAdder
      //  mythicMobs
    }


}
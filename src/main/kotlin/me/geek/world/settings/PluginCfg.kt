package me.geek.world.settings

import me.geek.world.utils.Expiry

data class PluginCfg(

    private val worldLoad: String = "24h",

    private val offLine: String = "24h",

    val startCluster: Boolean = false,

    val clusterId: String = "main",

    val isMailServer: Boolean = true
) {
    val worldLoads: Long = Expiry.getExpiryMillis(worldLoad, false)

    val offLines: Long = Expiry.getExpiryMillis(offLine, false)
}

package com.theevilroot.vmsis.simulator.model

data class Stats (
    val health: Int,
    val performance: Int
) {

    val data: List<Pair<String, String>>
        get() = listOf(
            "Health" to "$health",
            "Performance" to "$performance"
        )
}
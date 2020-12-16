package com.theevilroot.vmsis.simulator.model

data class Stats (
    val health: Int,
    val performance: Int,
    val saturation: Int,
    val money: Int
) {

    constructor(): this(100, 100, 50, 100)

    val data: List<Pair<String, String>>
        get() = listOf(
            "Health" to "$health",
            "Performance" to "$performance",
            "Saturation" to "$saturation",
            "Money" to "$money"
        )

    operator fun plus(action: Action): Stats {
        return with(action) {
            val healthMod = if ((saturation + action.saturationDelta) < 0) -10 else 0
            Stats(
                health + action.healthDelta + healthMod,
                performance + action.performanceDelta,
                saturation + saturationDelta,
                money + action.moneyDelta
            )
        }
    }

    fun denyReason(action: Action): String? {
        if (money < -action.moneyDelta)
            return "Not enough money"
        return null
    }
}
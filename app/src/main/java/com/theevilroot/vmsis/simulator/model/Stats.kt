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

    fun plus(data: Pair<Action, Player>): Stats {
        val (action, player) = data
        return with(action) {
            val satMod = player.difficulty * -5
            val sat = saturation + saturationDelta + satMod

            val healthMod = if ((saturation + action.saturationDelta + satMod) < 0) player.difficulty * -5 - 5 else 0
            val perfMod = if (action.performanceDelta <= 0) player.difficulty * -5 - 5 else 0

            Stats(
                health + action.healthDelta + healthMod,
                performance + action.performanceDelta + perfMod,
                sat,
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
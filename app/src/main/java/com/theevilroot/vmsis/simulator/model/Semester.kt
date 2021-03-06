package com.theevilroot.vmsis.simulator.model

import kotlin.random.Random

data class Semester(
    val index: Int,
    val sessionsCount: Int,
    val requiredPerformance: Int
) {

    constructor(player: Player): this(
        0,
        sessionsCount(player, 0),
        requiredPerformance(player, 0))

    operator fun plus(pp: Pair<Player, Boolean>): Semester {
        val (player, isFinished) = pp
        if (isFinished) {
            val newIndex = index + 1
            return Semester(newIndex,
                    sessionsCount(player, newIndex),
                    requiredPerformance(player, newIndex))
        }
        return this
    }

    companion object {
        fun sessionsCount(player: Player, index: Int): Int {
            return Random.nextInt(7, 12 + player.difficulty * 7 + index * 2)
        }
        fun requiredPerformance(player: Player, index: Int): Int {
            return 10 * (3 + player.difficulty * 2 + index / 2)
        }

    }

}
package com.theevilroot.vmsis.simulator.model

data class Semester(
    val index: Int,
    val sessionsCount: Int,
    val requiredPerformance: Int
) {

    operator fun plus(player: Player): Semester {
        return this
        TODO("Update semester")
    }

}
package com.theevilroot.vmsis.simulator.model

enum class Action (
    val healthDelta: Int,
    val performanceDelta: Int,
    val saturationDelta: Int,
    val moneyDelta: Int,
    val description: String
) {
    DO_STUDY(0, 10, -20, 0, "Do study"),
    BUY_COURSE_WORK(0, 50, -10, -80, "Buy coursework"),
    PLAY_GAMES(0, -10, -10, 0, "Play games"),
    PLAY_GAMES_ALL_NIGHT(-10, -30, -20, 0, "Play games all night"),
    GET_DRUNK(-20, -10, -20, -50, "Get drunk"),
    EAT(10, 0, 50, -20, "Eat"),
    DO_WORK(0, -20, -30, 30, "Do work"),
    DO_LABS(0, 30, -30, 0, "Do labs"),
}

package com.theevilroot.vmsis.simulator.model

import android.util.Log

data class SessionResult (
    val nextSession: Session?,
    val actionResult: Session.ActionResult,
)

data class Session (
    val player: Player,
    val startTime: Long,
    val semester: Semester,
    val metrics: Metrics,
    val stats: Stats,
    val sessionIndex: Int,
    val actions: List<Action>
) {

    constructor(prev: Session,
                semester: Semester,
                metrics: Metrics,
                stats: Stats,
                sessionIndex: Int,
                actions: List<Action>): this(
        prev.player,
        System.currentTimeMillis(),
        semester,
        metrics,
        stats,
        sessionIndex,
        actions)

    constructor(player: Player): this(
        player,
        System.currentTimeMillis(),
        Semester(player),
        Metrics(),
        Stats(),
        0,
        nextActions(Semester(player), player, 0))

    enum class ActionResult {
        NEXT_SESSION,
        NEXT_SEMESTER,
        DIED,
        EXPELLED,
        GRADUATED
    }

    operator fun plus(action: Action): SessionResult {
        val newStats = stats + action
        val isSemesterFinished = sessionIndex + 1 == semester.sessionsCount
        val result = checkStats(isSemesterFinished)

        if (result in setOf(ActionResult.GRADUATED, ActionResult.DIED, ActionResult.EXPELLED))
            return SessionResult(null, result)

        val newIndex = if (isSemesterFinished) 0 else sessionIndex + 1
        val newSemester = semester + (player to isSemesterFinished)
        val newSession = Session(this, newSemester,
            metrics + action, newStats, newIndex,
            nextActions(semester, player, newIndex))

        return SessionResult(newSession, result).also { Log.d("RESULT", it.toString()) }
    }

    private fun checkStats(isSemesterFinished: Boolean): ActionResult = with(stats) {
        if (health <= 0) {
            return@with ActionResult.DIED
        }
        if (isSemesterFinished && performance < semester.requiredPerformance) {
            return@with ActionResult.EXPELLED
        }
        if (isSemesterFinished && semester.index == 7) {
            return@with ActionResult.GRADUATED
        }
        return@with if (isSemesterFinished) ActionResult.NEXT_SEMESTER else ActionResult.NEXT_SESSION
    }

    companion object {
        fun nextActions(semester: Semester, player: Player, sessionIndex: Int): List<Action> {
            return Action.values().toList().shuffled().take(4)
            TODO("Generate actions")
        }
    }
}

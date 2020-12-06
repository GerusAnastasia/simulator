package com.theevilroot.vmsis.simulator.model

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
    val actions: List<IAction>
) {

    constructor(prev: Session,
                semester: Semester,
                metrics: Metrics,
                stats: Stats,
                sessionIndex: Int,
                actions: List<IAction>): this(
        prev.player,
        System.currentTimeMillis(),
        semester,
        metrics,
        stats,
        sessionIndex,
        actions)

    enum class ActionResult {
        NEXT_SESSION,
        NEXT_SEMESTER,
        DIED,
        EXPELLED,
        GRADUATED
    }

    operator fun plus(action: IAction): SessionResult {
        val newStats = action.apply(player, stats)
        val isSemesterFinished = sessionIndex + 1 == semester.sessionsCount
        val result = checkStats(isSemesterFinished)

        if (result in setOf(ActionResult.GRADUATED, ActionResult.DIED, ActionResult.EXPELLED))
            return SessionResult(null, result)

        val newIndex = if (isSemesterFinished) 0 else sessionIndex + 1
        val newSemester = semester + player
        val newSession = Session(this, newSemester,
            metrics + action, newStats, newIndex,
            nextActions(semester, player, newIndex))

        return SessionResult(newSession, result)
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
        private fun nextActions(semester: Semester, player: Player, index: Int): List<IAction> {
            return listOf()
            TODO("Generate actions")
        }
    }
}

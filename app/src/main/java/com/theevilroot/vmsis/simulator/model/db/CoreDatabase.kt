package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.*

open class CoreDatabase : ISimulatorDatabase {

    /**
     * Table with players (id as unique row)
     */
    protected val playerList: MutableList<Player> = mutableListOf()

    /**
     * Table with all the states (player id as unique row)
     */
    protected val stateList: MutableMap<Int, PersistentState> = mutableMapOf()

    override fun getPlayers(): List<Player> {
        return playerList
    }

    override fun addPlayer(player: Player) {
        playerList.add(player)
    }

    protected open fun getPersistentState(playerId: Int): PersistentState? {
        return stateList[playerId]
    }

    protected open fun setPersistentState(playerId: Int, persistentState: PersistentState) {
        stateList[playerId] = persistentState
    }

    /**
     * Create session from player by it's persistent state
     * or create a new session without persistent state
     */
    override fun getSession(player: Player): Session {
        val persistentState = getPersistentState(player.id)
            ?: return Session(player)

        with(persistentState) {
            val semester = Semester(currentSemester, semestersSessions, requiredPerformance)
            val stats = Stats(health, performance, saturation, money)
            val newPlayer = Player(playerId, playerName, difficulty)
            val metrics = Metrics()

            return Session(newPlayer, sessionStartTime,
                semester, metrics, stats,
                currentSessionIndex, currentActions)
        }
    }

    override fun getGameInfo(player: Player): GameInfo? {
        val persistentState = getPersistentState(player.id)
                ?: return null

        val newPlayer = Player(persistentState.playerId,
                persistentState.playerName,
                persistentState.difficulty)
        val stats = Stats(persistentState.health, persistentState.performance,
            persistentState.saturation, persistentState.money)
        val semester = Semester(persistentState.currentSemester,
                persistentState.semestersSessions,
                persistentState.requiredPerformance)
        val metrics = Metrics()

        return GameInfo(newPlayer, stats,
                metrics, semester,
                persistentState.isFinished,
                persistentState.result)
    }

    override fun updatePersistentState(currentSession: Session, result: SessionResult) {
        val persistentState = if (result.actionResult in setOf(Session.ActionResult.NEXT_SESSION,
                Session.ActionResult.NEXT_SEMESTER) && result.nextSession != null) {
                    result.nextSession.run {
                PersistentState(
                    player.id,
                    player.name,
                    player.difficulty,
                    semester.index,
                    semester.sessionsCount,
                    semester.requiredPerformance,
                    stats.health,
                    stats.performance,
                    stats.saturation,
                    stats.money,
                    actions,
                    startTime,
                    sessionIndex,
                    false,
                    result.actionResult
                )
            }
        } else {
            currentSession.run {
                PersistentState(
                    player.id,
                    player.name,
                    player.difficulty,
                    semester.index,
                    semester.sessionsCount,
                    semester.requiredPerformance,
                    stats.health,
                    stats.performance,
                    stats.saturation,
                    stats.money,
                    actions,
                    startTime,
                    sessionIndex,
                    true,
                    result.actionResult
                )
            }
        }
        setPersistentState(persistentState.playerId, persistentState)
    }

}
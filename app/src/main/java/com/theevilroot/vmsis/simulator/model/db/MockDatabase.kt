package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.*

class MockDatabase : ISimulatorDatabase {

    /**
     * Table with players (id as unique row)
     */
    private val playerList: MutableList<Player> = mutableListOf()

    /**
     * Table with all the states (player id as unique row)
     */
    private val stateList: MutableMap<Int, PersistentState> = mutableMapOf()

    override fun getPlayers(): List<Player> {
        return playerList
    }

    override fun addPlayer(player: Player) {
        playerList.add(player)
    }

    /**
     * Create session from player by it's persistent state
     * or create a new session without persistent state
     */
    override fun getSession(player: Player): Session {
        val persistentState = stateList[player.id]
            ?: return Session(player)

        with(persistentState) {
            val semester = Semester(currentSemester, semestersSessions, requiredPerformance)
            val stats = Stats(health, performance)
            val newPlayer = Player(playerId, playerName, difficulty)
            val metrics = Metrics()

            return Session(newPlayer, sessionStartTime,
                semester, metrics, stats,
                currentSessionIndex, currentActions)
        }
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
                    actions,
                    startTime,
                    sessionIndex,
                    true,
                    result.actionResult
                )
            }
        }
        stateList[persistentState.playerId] = persistentState
    }

}
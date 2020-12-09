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

    override fun newSession(p: Player): Session? {
        val persistentState = stateList[p.id]
            ?: return null

        with(persistentState) {
            val semester = Semester(currentSemester, semestersSessions, requiredPerformance)
            val stats = Stats(health, performance)
            val player = Player(playerId, playerName, difficulty)
            val metrics = Metrics()

            return Session(player, sessionStartTime,
                semester, metrics, stats,
                currentSessionIndex, currentActions)
        }
    }

}
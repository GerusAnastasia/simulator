package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.*

interface ISimulatorDatabase {

    fun getPlayers(): List<Player>

    fun addPlayer(player: Player)

    fun getSession(player: Player): Session

    /** Should we do it? Shouldn't persistent state be database only entity? **/
    /** Rethink and reimplement **/
    fun getGameInfo(player: Player): GameInfo?

    fun updatePersistentState(currentSession: Session, result: SessionResult)

}
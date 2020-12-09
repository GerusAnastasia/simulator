package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.Session
import com.theevilroot.vmsis.simulator.model.SessionResult

interface ISimulatorDatabase {

    fun getPlayers(): List<Player>

    fun addPlayer(player: Player)

    fun getSession(player: Player): Session

    fun updatePersistentState(currentSession: Session, result: SessionResult)

}
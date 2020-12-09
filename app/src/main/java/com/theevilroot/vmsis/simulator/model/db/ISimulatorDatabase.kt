package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.Session

interface ISimulatorDatabase {

    fun getPlayers(): List<Player>

    fun addPlayer(player: Player)

    fun newSession(player: Player): Session?

}
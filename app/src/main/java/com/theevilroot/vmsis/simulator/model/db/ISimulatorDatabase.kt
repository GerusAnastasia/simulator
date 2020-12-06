package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.Player

interface ISimulatorDatabase {

    fun getPlayers(): List<Player>

    fun addPlayer(player: Player)

}
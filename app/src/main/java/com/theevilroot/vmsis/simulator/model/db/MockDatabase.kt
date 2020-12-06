package com.theevilroot.vmsis.simulator.model.db

import com.theevilroot.vmsis.simulator.model.Player

class MockDatabase : ISimulatorDatabase {

    private val mutableList: MutableList<Player> = mutableListOf()

    override fun getPlayers(): List<Player> {
        return mutableList
    }

    override fun addPlayer(player: Player) {
        mutableList.add(player)
    }

}
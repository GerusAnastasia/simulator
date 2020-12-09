package com.theevilroot.vmsis.simulator.model.actions

import com.theevilroot.vmsis.simulator.model.IAction
import com.theevilroot.vmsis.simulator.model.Player
import com.theevilroot.vmsis.simulator.model.Stats

class BaseAction (
    private val name: String,
    private val healthDelta: Int = 0,
    private val performanceDelta: Int = 0
) : IAction {
    override fun apply(player: Player, stats: Stats): Stats = stats.run {
        Stats(health + healthDelta, performance + performanceDelta)
    }

    override fun getDescription(): String {
        return name
    }
}
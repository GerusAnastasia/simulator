package com.theevilroot.vmsis.simulator.model

interface IAction {

    fun apply(player: Player, stats: Stats): Stats

}
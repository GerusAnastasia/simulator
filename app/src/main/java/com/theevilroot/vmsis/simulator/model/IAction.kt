package com.theevilroot.vmsis.simulator.model

interface IAction {
    fun getDescription(): String
    fun apply(player: Player, stats: Stats): Stats
}
package com.theevilroot.vmsis.simulator.model

data class GameInfo(
        val player: Player,
        val stats: Stats,
        val metrics: Metrics,
        val semester: Semester,
        val isFinished: Boolean,
        val result: Session.ActionResult
)
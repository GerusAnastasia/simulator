package com.theevilroot.vmsis.simulator.model

data class PersistentState(
    val playerId: Int,
    val playerName: String,
    val difficulty: Int,
    val currentSemester: Int,
    val semestersSessions: Int,
    val requiredPerformance: Int,
    val health: Int,
    val performance: Int,
    val currentActions: List<IAction>,
    val sessionStartTime: Long,
    val currentSessionIndex: Int,
    val isFinished: Boolean,
    val result: Session.ActionResult
)
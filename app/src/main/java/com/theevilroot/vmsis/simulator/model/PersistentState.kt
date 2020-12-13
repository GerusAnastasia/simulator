package com.theevilroot.vmsis.simulator.model

import com.google.gson.JsonObject

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
) {

    constructor(json: JsonObject):
            this(json["playerId"].asInt,
                json["playerName"].asString,
                json["difficulty"].asInt,
                json["currentSemester"].asInt,
                json["semestersSessions"].asInt,
                json["requiredPerformance"].asInt,
                json["health"].asInt,
                json["performance"].asInt,
                emptyList(),
                json["sessionStartTime"].asLong,
                json["currentSessionIndex"].asInt,
                json["isFinished"].asBoolean,
                Session.ActionResult.valueOf(json["result"].asString))


    fun toJson(): JsonObject = JsonObject().apply {
        addProperty("playerId", playerId)
        addProperty("playerName", playerName)
        addProperty("difficulty", difficulty)
        addProperty("currentSemester", currentSemester)
        addProperty("semestersSessions", semestersSessions)
        addProperty("requiredPerformance", requiredPerformance)
        addProperty("health", health)
        addProperty("performance", performance)
        addProperty("sessionStartTime", sessionStartTime)
        addProperty("currentSessionIndex", currentSessionIndex)
        addProperty("isFinished", isFinished)
        addProperty("result", result.name)
    }

}
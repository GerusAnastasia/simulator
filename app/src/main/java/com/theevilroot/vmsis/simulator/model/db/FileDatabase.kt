package com.theevilroot.vmsis.simulator.model.db

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.theevilroot.vmsis.simulator.model.PersistentState
import com.theevilroot.vmsis.simulator.model.Player
import java.io.File

class FileDatabase (
    private val gson: Gson,
    private val dir: File,
    private val filename: String
) : CoreDatabase() {

    private fun readFileJson() = runCatching {
        val file = File(dir, filename)
        if (file.exists() && file.canRead()) {
            val content = JsonParser.parseReader(file.bufferedReader())
            content.asJsonObject
        } else null
    }.getOrNull()

    private fun getStateMap(json: JsonObject) = runCatching {
        val map = json["states"].asJsonObject
        map.entrySet().map { (k, v) ->
            k.toInt() to v.asJsonObject.let(::PersistentState)
        }.toMap()
    }.getOrDefault(emptyMap())

    private fun getPlayersList(json: JsonObject) = runCatching {
        val list = json["players"].asJsonArray
        list.map { it.asJsonObject.let(::Player) }
    }.getOrDefault(emptyList())

    private fun updateFileIntoMemory() {
        stateList.clear()
        playerList.clear()
        readFileJson()?.let {
            stateList.putAll(getStateMap(it))
            playerList.addAll(getPlayersList(it))
        }
    }

    private fun updateFileFromMemory() {
        val states = stateList.entries.map { (k, v) ->
            k.toString() to v.toJson()
        }.fold(JsonObject()) { obj, (k, v) -> obj.add(k, v); obj }
        val players = playerList.map {
            it.toJson()
        }.fold(JsonArray()) { arr, el -> arr.add(el); arr }
        val json = JsonObject().apply {
            add("states", states)
            add("players", players)
        }.let(gson::toJson)

        runCatching {
            val file = File(dir, filename)
            if (!file.exists())
                file.createNewFile()
            file.writeText(json)
        }
    }

    override fun getPlayers(): List<Player> {
        updateFileIntoMemory()
        return super.getPlayers()
    }

    override fun getPersistentState(playerId: Int): PersistentState? {
        updateFileIntoMemory()
        return super.getPersistentState(playerId)
    }

    override fun setPersistentState(playerId: Int, persistentState: PersistentState) {
        super.setPersistentState(playerId, persistentState)
        updateFileFromMemory()
    }

    override fun addPlayer(player: Player) {
        super.addPlayer(player)
        updateFileFromMemory()
    }

}
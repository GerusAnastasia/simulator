package com.theevilroot.vmsis.simulator.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

data class Player(
    val id: Int,
    val name: String,
    val difficulty: Int,
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: throw IllegalArgumentException("failed to deserialize name"),
        parcel.readInt()
    )

    constructor(json: JsonObject):
            this(json["id"].asInt,
                json["name"].asString,
                json["difficulty"].asInt)

    override fun describeContents(): Int {
        return id
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeInt(difficulty)
    }

    fun toJson(): JsonObject = JsonObject().apply {
        addProperty("id", id)
        addProperty("name", name)
        addProperty("difficulty", difficulty)
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }

}

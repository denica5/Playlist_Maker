package com.denica.playlistmaker.mediaLibrary.domain

import android.os.Parcel
import android.os.Parcelable

data class Playlist(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val imagePath: String,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createLongList(),
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(imagePath)
        parcel.writeLongList(trackIds)
        parcel.writeInt(trackCount)
    }

    companion object CREATOR : Parcelable.Creator<Playlist> {
        override fun createFromParcel(parcel: Parcel): Playlist {
            return Playlist(parcel)
        }

        override fun newArray(size: Int): Array<Playlist?> {
            return arrayOfNulls(size)
        }
    }


}

fun Parcel.writeLongList(input: List<Long>) {
    writeInt(input.size) // Save number of elements.
    input.forEach(this::writeLong) // Save each element.
}

fun Parcel.createLongList(): List<Long> {
    val size = readInt()
    val output = ArrayList<Long>(size)
    for (i in 0 until size) {
        output.add(readLong())
    }
    return output
}

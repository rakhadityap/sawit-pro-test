package com.example.sawitprotest.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Ticket(
    var id: String = "",
    var datetime: String = "",
    var licenseNumber: String = "",
    var driverName: String = "",
    var inboundWeight: Int = 0,
    var outboundWeight: Int = 0,
    var netWeight: Int = 0
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    init {
        if(datetime == "") datetime = SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale("id", "ID")).format(
            Date()
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(datetime)
        parcel.writeString(licenseNumber)
        parcel.writeString(driverName)
        parcel.writeInt(inboundWeight)
        parcel.writeInt(outboundWeight)
        parcel.writeInt(netWeight)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ticket> {
        override fun createFromParcel(parcel: Parcel): Ticket {
            return Ticket(parcel)
        }

        override fun newArray(size: Int): Array<Ticket?> {
            return arrayOfNulls(size)
        }
    }
}

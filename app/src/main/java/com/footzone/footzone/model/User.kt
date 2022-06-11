package com.footzone.footzone.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    val deviceName: String?,
    val deviceToken: String?,
    val deviceType: String?,
    val fullName: String?,
    val language: String?,
    val phoneNumber: String?,
    var smsCode: String?,
    val stadiumHolder: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(deviceName)
        parcel.writeString(deviceToken)
        parcel.writeString(deviceType)
        parcel.writeString(fullName)
        parcel.writeString(language)
        parcel.writeString(phoneNumber)
        parcel.writeString(smsCode)
        parcel.writeValue(stadiumHolder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
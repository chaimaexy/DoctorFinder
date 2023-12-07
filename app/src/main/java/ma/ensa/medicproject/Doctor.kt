package ma.ensa.medicproject

import android.os.Parcel
import android.os.Parcelable

data class Doctor(
    var name: String = "",
    var email: String = "",
    val password: String = "",
    val gender: String = "",
    val pmdc: String = "",
    var experience: String = "",
    val specialityId: Int = 0,
    var consultPrice: String = "",
    val consultPriceInfo: String = "",
    val address: String = "",
    var location: String = "",
    var city: String = "",
    var selectedDays: List<String> = emptyList(),
    var startTime: String = "",
    var endTime: String = "",
    var clickCounter: Int = 0,
    var phoneNumber: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "" // Added phone number property
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(gender)
        parcel.writeString(pmdc)
        parcel.writeString(experience)
        parcel.writeInt(specialityId)
        parcel.writeString(consultPrice)
        parcel.writeString(consultPriceInfo)
        parcel.writeString(address)
        parcel.writeString(location)
        parcel.writeString(city)
        parcel.writeStringList(selectedDays)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeInt(clickCounter)
        parcel.writeString(phoneNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Doctor> {
        override fun createFromParcel(parcel: Parcel): Doctor {
            return Doctor(parcel)
        }

        override fun newArray(size: Int): Array<Doctor?> {
            return arrayOfNulls(size)
        }
    }
}
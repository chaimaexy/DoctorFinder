package ma.ensa.medicproject

import android.os.Parcel
import android.os.Parcelable

data class Doctor(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val gender: String = "",
    val pmdc: String = "",
    val experience: String = "",
    val specialityId: Int = 0,
    val consultPrice: String = "",
    val consultPriceInfo: String = "",
    val address: String = "",
    val location: String = "",
    val city: String = "",
    val selectedDays: List<String> = emptyList(),
    val startTime: String = "",
    val endTime: String = ""
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
        parcel.readString() ?: ""
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
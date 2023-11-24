package ma.ensa.medicproject

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
)
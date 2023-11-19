package ma.ensa.medicproject
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

data class Images(
    val imageUri: String,
    val imagePMDC: String
)

package ma.ensa.medicproject
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.util.*
class FirebaseImageUploader {
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    fun uploadImage(imageUri: Uri, imagePMDC: String) {
        // Generate a unique name for the image using both UUID and imagePMDC
        val imageName = "image_${imagePMDC}.jpg"

        // Create a reference to the Firebase Storage path
        val imageRef = storageReference.child("images/$imageName")

        // Upload the image
        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        // Register observers to listen for when the upload is done or if it fails
        uploadTask.addOnSuccessListener {
        }.addOnFailureListener {
            // Handle unsuccessful uploads
        }
    }

    fun getImageDownloadUrl(imagePMDC: String, onSuccess: (Uri) -> Unit, onFailure: () -> Unit) {
        val imageName = "image_${imagePMDC}.jpg"
        val imageRef = storageReference.child("images/$imageName")

        imageRef.downloadUrl.addOnSuccessListener {
            onSuccess.invoke(it)
        }.addOnFailureListener {
            onFailure.invoke()
        }
    }





}
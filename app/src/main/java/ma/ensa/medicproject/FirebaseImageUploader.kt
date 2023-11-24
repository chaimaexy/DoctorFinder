package ma.ensa.medicproject
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
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

    fun loadImage(imagePMDC: String, imageView: ImageView) {
        // Generate the image name based on the imagePMDC
        val imageName = "image_${imagePMDC}.jpg"

        // Create a reference to the Firebase Storage path
        val imageRef = storageReference.child("images/").child(imageName)

        // Load image into imageView using Picasso
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageView)
        }.addOnFailureListener {
            imageView.setImageResource(R.drawable.homeimg)
        }
    }
}
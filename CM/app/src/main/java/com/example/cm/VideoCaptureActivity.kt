package com.example.cm
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast

class VideoCaptureActivity : AppCompatActivity() {

    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var captureVideoButton: Button
    private lateinit var uploadVideoButton: Button
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture)

        captureVideoButton = findViewById(R.id.captureVideoButton)
        uploadVideoButton = findViewById(R.id.uploadVideoButton)

        firebaseStorage = FirebaseStorage.getInstance()

        captureVideoButton.setOnClickListener {
            if (hasPermissions()) {
                captureVideo()
            } else {
                requestPermissions()
            }
        }

        uploadVideoButton.setOnClickListener {
            videoUri?.let { uri ->
                uploadVideoToFirebase(uri)
            }
        }
    }




    private fun captureVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            videoUri = data?.data
        }
    }




    private fun uploadVideoToFirebase(uri: Uri) {
        // Generate a unique filename using a timestamp
        val timestamp = System.currentTimeMillis()
        val fileName = "video_intoxication.mp4"

        val videoRef = firebaseStorage.reference.child("videos/$fileName")
        val uploadTask = videoRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Video uploaded successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && writeStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSIONS_REQUEST
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)  // Call to the superclass implementation
        when (requestCode) {
            PERMISSIONS_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    captureVideo()
                } else {
                    Toast.makeText(this, "Permission denied to access Camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    companion object {
        private const val REQUEST_VIDEO_CAPTURE = 1
        private const val PERMISSIONS_REQUEST = 101
    }
}

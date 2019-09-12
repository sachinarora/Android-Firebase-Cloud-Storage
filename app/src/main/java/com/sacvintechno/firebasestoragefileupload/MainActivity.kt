package com.sacvintechno.firebasestoragefileupload

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() , View.OnClickListener{


    //a constant to track the file chooser intent
    private val PICK_IMAGE_REQUEST = 234

    //a Uri object to store file path
    private var filePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //attaching listener
        buttonChoose?.setOnClickListener(this);
        buttonUpload?.setOnClickListener(this);
    }

    //method to show file chooser
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageView?.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onClick(view: View) {
        //if the clicked button is choose
        if (view === buttonChoose) {
            showFileChooser()
        } else if (view === buttonUpload) {
            uploadFile()
        }//if the clicked button is upload
    }


    //this method will upload the file
    private fun uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading")
            progressDialog.show()

            val imageName=System.currentTimeMillis().toString()
            val riversRef = FirebaseStorage.getInstance().getReference().child("images/"+imageName+".jpg");
            riversRef.putFile(filePath!!)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                    //if the upload is successfull
                    //hiding the progress dialog
                    progressDialog.dismiss()
                    //and displaying a success toast
                    Toast.makeText(applicationContext, "File Uploaded ", Toast.LENGTH_LONG).show()
                })
                .addOnFailureListener(OnFailureListener { exception ->
                    //if the upload is not successfull
                    //hiding the progress dialog
                    progressDialog.dismiss()

                    //and displaying error message
                    Toast.makeText(applicationContext, exception.message, Toast.LENGTH_LONG).show()
                })
                .addOnProgressListener(OnProgressListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    //calculating progress percentage
                    val progress =
                        100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
                })
        } else {
            //you can display an error toast
        }//if there is not any file
    }
}



package com.example.uas

import android.app.ProgressDialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateData : AppCompatActivity() {
    private var id: String? =""
    private var title: String? = null
    private var desc: String? = null
    private var image: String? = null
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var titleEdit : EditText
    private lateinit var descEdit : EditText
    private lateinit var buttonSave : Button
    private lateinit var buttonChooseImg : Button
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var imageView: ImageView
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_data)

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        titleEdit = findViewById(R.id.title);
        descEdit = findViewById(R.id.desc);
        buttonSave = findViewById(R.id.btnSumbit);
        buttonChooseImg = findViewById(R.id.btnChooseImage)
        imageView = findViewById(R.id.image)
        progressDialog =   ProgressDialog(this);
        progressDialog.setTitle("Loading ....");

        val updateOption = intent
        if(updateOption != null) {
            id = updateOption.getStringExtra("id")
            title = updateOption.getStringExtra("title")
            desc = updateOption.getStringExtra("desc")
            image = updateOption.getStringExtra("image")

            titleEdit.setText(title)
            descEdit.setText(desc)
            Glide.with(this).load(image).into(imageView)
        }

        buttonChooseImg.setOnClickListener(){
            openFileChooser()

        }

        buttonSave.setOnClickListener(){
            val newTitle = titleEdit.text.toString().trim()
            val newDesc = descEdit.text.toString().trim()
            if(newTitle.isEmpty() || newDesc.isEmpty()){
                Toast.makeText(this@CreateData, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            progressDialog.show()
            if(imageUri != null) {
                uploadImageToStorage(newTitle, newDesc)
            } else {
                saveData(newTitle, newDesc, image ?:"")
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
    private fun openFileChooser(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int,resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }
    private fun uploadImageToStorage(newTitle: String, newDesc: String) {
        imageUri.let { a ->
            val storageRef = storage.reference.child("imagetodo/" + System.currentTimeMillis() + ".jpg")
            storageRef.putFile(a!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imgUrl = downloadUri.toString()
                    saveData(newTitle, newDesc, imgUrl)
                }
            }
                .addOnFailureListener{e ->
                    progressDialog.dismiss()
                    Toast.makeText(this@CreateData, "FAILED something wrong: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    fun saveData(title: String, desc: String, imageUrl: String){
        val todo = HashMap<String, Any>()
        todo["title"] = title
        todo["desc"] = desc
        todo["image"] = imageUrl
        if(id!= null) {
            db.collection("todo").document(id?: "").update(todo).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@CreateData, "SUCCESS", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@CreateData, "FAILED: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.w("ADD", "ERROR")
            }
        } else {
            db.collection("todo").add(todo).addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this@CreateData, "SUCCESS", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@CreateData, "FAILED: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.w("ADD", "ERROR")
            }
        }
    }
}
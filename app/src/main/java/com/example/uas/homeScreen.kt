package com.example.uas

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class homeScreen : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var myAdapterTodo: AdapterTodo
    private lateinit var itemList: MutableList<ItemList>
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.viewTodo)
        floatingButton = findViewById(R.id.floatingBtn)

        progressDialog = ProgressDialog(this@homeScreen).apply { setTitle("LOADING ...") }
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = ArrayList();
        myAdapterTodo = AdapterTodo(itemList)
        recyclerView.adapter = myAdapterTodo
        floatingButton.setOnClickListener(){
            val intent = Intent(this, CreateData::class.java)
            startActivity(intent)
        }
        myAdapterTodo.setOnItemClickListener(object : AdapterTodo.OnItemClickListener{
            override fun onItemClick(item: ItemList){
                val intent = Intent(this@homeScreen, detailTodo::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("title", item.title)
                    putExtra("desc", item.desc)
                    putExtra("image", item.image)
                }
                startActivity(intent)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getData()
    }

    //    override fun onStart(){
//        super.onStart()
//        getData()
//
//    }
    private fun getData(){
        progressDialog.show()
        db.collection("todo").get().addOnCompleteListener{
                task -> if (task.isSuccessful){
            itemList.clear()
            for (data in task.result){
                val todo = ItemList(
                    data.id,
                    data.getString("desc") ?:"",
                    data.getString("image") ?:"",
                    data.getString("title")?:""
                )
                itemList.add(todo)
                Log.d("MANGGIL", "${data.id}=> ${data.data}" )
            }
            myAdapterTodo.notifyDataSetChanged()
        } else {
            Log.w("GAGAL", "ERROR: ",task.exception)
        }
            progressDialog.dismiss()
        }

    }
}
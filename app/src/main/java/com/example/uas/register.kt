package com.example.uas

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uts1.databinding.ActivityRegisterBinding
import android.util.Log
import android.widget.Toast

class register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnRegister.setOnClickListener {
            Toast.makeText(this, "Data telah diregister", Toast.LENGTH_SHORT).show()

            Log.d(TAG, "Data telah diregister")

            startActivity(Intent(this, login::class.java))
        }
        binding.tvHaveAccount.setOnClickListener{
            startActivity(Intent(this, login::class.java))
        }

    }
}
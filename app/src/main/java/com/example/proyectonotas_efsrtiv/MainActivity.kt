package com.example.proyectonotas_efsrtiv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectonotas_efsrtiv.database.DBHelper


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val BRegistrar = findViewById<Button>(R.id.BtRegistrar)
        val botonIngresar = findViewById<Button>(R.id.BtIngresar)
        val editDocumento = findViewById<EditText>(R.id.editTextText)
        val editContrasena = findViewById<EditText>(R.id.editTextTextPassword)

        BRegistrar.setOnClickListener {
            val intent = Intent(this, RegistroAlumnoActivity::class.java)
            startActivity(intent)
        }

        botonIngresar.setOnClickListener {
            val documento = editDocumento.text.toString()
            val contrasena = editContrasena.text.toString()

            if (documento.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Ingrese documento y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbHelper = DBHelper(this)
            val valido = dbHelper.validarLogin(documento, contrasena)

            if (valido) {
                Toast.makeText(this, "Ingreso exitoso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
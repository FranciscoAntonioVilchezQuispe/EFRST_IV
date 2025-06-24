package com.example.proyectonotas_efsrtiv

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectonotas_efsrtiv.database.DBHelper

class RegistroAlumnoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_alumno)

        val dbHelper = DBHelper(this)
        val BRegresar = findViewById<ImageButton>(R.id.BtRegresar)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val carrera = listOf(
            "COMPUTACION INFORMATICA",
            "SEGURIDAD INFORMATICA",
            "BIG DATA",
            "ADMINISTRACION DE REDES"
        )
        val botonCrear = findViewById<Button>(R.id.BtCrear)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, carrera)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        botonCrear.setOnClickListener {
            val documento = findViewById<EditText>(R.id.editTextNumber).text.toString()
            val nombres = findViewById<EditText>(R.id.editTextText2).text.toString()
            val apellidop = findViewById<EditText>(R.id.editTextText3).text.toString()
            val apellidom = findViewById<EditText>(R.id.editTextText4).text.toString()
            val contrasena = findViewById<EditText>(R.id.editTextTextPassword2).text.toString()
            val repetirContrasena =
                findViewById<EditText>(R.id.editTextTextPassword3).text.toString()
            val carreraSeleccionada = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

            if (
                documento.isBlank() || nombres.isBlank() || apellidop.isBlank() ||
                apellidom.isBlank() || contrasena.isBlank() || repetirContrasena.isBlank()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != repetirContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoAlumno = Alumno(
                documento = documento,
                nombres = nombres,
                apellidop = apellidop,
                apellidom = apellidom,
                carrera = carreraSeleccionada,
                contrasena = contrasena
            )

            val exito = dbHelper.insertarAlumno(nuevoAlumno)

            if (exito) {
                Toast.makeText(this, "Alumno registrado correctamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }


        BRegresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
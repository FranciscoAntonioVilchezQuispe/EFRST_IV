package com.example.proyectonotas_efsrtiv

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectonotas_efsrtiv.database.DBHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegistroAlumnoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_alumno)

        dbHelper = DBHelper(this)

        // Referencias a los componentes
        val editDocumento = findViewById<TextInputEditText>(R.id.editTextNumber)
        val editNombres = findViewById<TextInputEditText>(R.id.editTextText2)
        val editApPaterno = findViewById<TextInputEditText>(R.id.editTextText3)
        val editApMaterno = findViewById<TextInputEditText>(R.id.editTextText4)
        val spinnerCarrera = findViewById<AutoCompleteTextView>(R.id.spinner)
        val editPassword = findViewById<TextInputEditText>(R.id.editTextTextPassword2)
        val editRepeatPassword = findViewById<TextInputEditText>(R.id.editTextTextPassword3)
        val btnCrear = findViewById<MaterialButton>(R.id.BtCrear)
        val btnRegresar = findViewById<MaterialButton>(R.id.BtRegresar)

        // Configurar el dropdown de carreras
        setupCarrerasDropdown(spinnerCarrera)

        // Botón Regresar
        btnRegresar.setOnClickListener {
            finish()
        }

        // Botón Crear
        btnCrear.setOnClickListener {
            registrarAlumno(
                editDocumento.text.toString().trim(),
                editNombres.text.toString().trim(),
                editApPaterno.text.toString().trim(),
                editApMaterno.text.toString().trim(),
                spinnerCarrera.text.toString().trim(),
                editPassword.text.toString().trim(),
                editRepeatPassword.text.toString().trim()
            )
        }
    }

    private fun setupCarrerasDropdown(spinner: AutoCompleteTextView) {
        val carreras = arrayOf(
            "Ingeniería de Sistemas",
            "Ingeniería Industrial",
            "Administración de Empresas",
            "Contabilidad",
            "Marketing",
            "Psicología",
            "Derecho",
            "Medicina",
            "Enfermería",
            "Arquitectura"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, carreras)
        spinner.setAdapter(adapter)
    }

    private fun registrarAlumno(
        documento: String,
        nombres: String,
        apPaterno: String,
        apMaterno: String,
        carrera: String,
        password: String,
        repeatPassword: String
    ) {
        // Validaciones
        if (documento.isEmpty() || nombres.isEmpty() || apPaterno.isEmpty() ||
            apMaterno.isEmpty() || carrera.isEmpty() || password.isEmpty() ||
            repeatPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (documento.length < 8) {
            Toast.makeText(this, "El documento debe tener al menos 8 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repeatPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si el documento ya existe
        if (dbHelper.existeDocumento(documento)) {
            Toast.makeText(this, "El documento ya está registrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar el alumno
        var alumno:Alumno=Alumno()
        alumno.documento=documento
        alumno.nombres=nombres
        alumno.apellidop=apPaterno
        alumno.apellidom=apMaterno
        alumno.carrera=carrera
        alumno.contrasena=password
        val resultado = dbHelper.insertarAlumno(alumno)

        if (resultado ) {
            Toast.makeText(this, "Alumno registrado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el alumno", Toast.LENGTH_SHORT).show()
        }
    }
}
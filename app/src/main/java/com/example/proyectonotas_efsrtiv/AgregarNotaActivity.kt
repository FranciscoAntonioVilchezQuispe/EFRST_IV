package com.example.proyectonotas_efsrtiv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectonotas_efsrtiv.database.DBHelper

class AgregarNotaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var alumno: Alumno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)

        dbHelper = DBHelper(this)

        val documento = intent.getStringExtra("documento")
        if (documento == null) {
            Toast.makeText(this, "Documento no recibido", Toast.LENGTH_SHORT).show()
            Log.e("AgregarNotaActivity", "Error: no se recibió el documento desde el Intent")
            finish()
            return
        }

        alumno = dbHelper.getAlumnoByDocumento(documento) ?: run {
            Toast.makeText(this, "Alumno no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.textViewNombreAlumno).text =
            "${alumno.nombres} ${alumno.apellidop} ${alumno.apellidom}"


        val cursos = listOf(
            "Matematica I",
            "Innovacion",
            "Desarrollo web",
            "Desarrollo movil",
            "Seguridad de aplicaciones"
        )

        val spinnerCurso = findViewById<Spinner>(R.id.spinnerCurso)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cursos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurso.adapter = adapter

        val cursoRecibido = intent.getStringExtra("curso")
        var cursoSeleccionado = ""
        if (cursoRecibido != null) {
            val index = cursos.indexOf(cursoRecibido)
            if (index >= 0) {
                spinnerCurso.setSelection(index)
                cursoSeleccionado = cursoRecibido
            }
            spinnerCurso.isEnabled = false // No permitir cambiar
        }

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupParcial)
        val editNota = findViewById<EditText>(R.id.editTextNota)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarNota)
        val textViewPromedio = findViewById<TextView>(R.id.textViewPromedio)

        if (cursoRecibido != null) {
            val notaT1 = dbHelper.obtenerNotaParcial(documento, cursoRecibido, "T1")
            val notaT2 = dbHelper.obtenerNotaParcial(documento, cursoRecibido, "T2")
            val notaFinal = dbHelper.obtenerNotaParcial(documento, cursoRecibido, "Final")

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val nota = when (checkedId) {
                    R.id.radioT1 -> notaT1
                    R.id.radioT2 -> notaT2
                    R.id.radioFinal -> notaFinal
                    else -> null
                }
                editNota.setText(nota?.toString() ?: "")
            }
        }

        // 8. Botón guardar
        btnGuardar.setOnClickListener {
            val curso = spinnerCurso.selectedItem.toString()
            val notaStr = editNota.text.toString()

            val parcial = when (radioGroup.checkedRadioButtonId) {
                R.id.radioT1 -> "T1"
                R.id.radioT2 -> "T2"
                R.id.radioFinal -> "Final"
                else -> ""
            }

            if (parcial.isEmpty() || notaStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nota = notaStr.toIntOrNull()
            if (nota == null || nota !in 1..20) {
                Toast.makeText(this, "Nota inválida. Debe ser entre 1 y 20", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultado = dbHelper.registrarNotaParcial(documento, curso, parcial, nota)

            if (resultado) {
                Toast.makeText(this, "Nota registrada correctamente", Toast.LENGTH_SHORT).show()
                val promedioActual = dbHelper.obtenerPromedio(documento, curso)
                textViewPromedio.text = "Promedio actual: %.2f".format(promedioActual)


                val intent = Intent(this, ListaCursosActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

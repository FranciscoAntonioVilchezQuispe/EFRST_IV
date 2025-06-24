package com.example.proyectonotas_efsrtiv

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectonotas_efsrtiv.database.DBHelper

class CursosDelAlumnoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var textNombreAlumno: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos_alumno)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewCursosAlumno)
        textNombreAlumno = findViewById(R.id.textNombreAlumno)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val documento = intent.getStringExtra("documento")
        if (documento == null) {
            Toast.makeText(this, "Documento no recibido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val alumno = dbHelper.getAlumnoByDocumento(documento)
        if (alumno != null) {
            textNombreAlumno.text = "Alumno: ${alumno.nombres} ${alumno.apellidop} ${alumno.apellidom}"
        }

        val cursos = dbHelper.obtenerListaCursosConNotas().filter { it.documento == documento }
        recyclerView.adapter = CursoAdapter(cursos) {} // sin botón de editar para el alumno
    }
}

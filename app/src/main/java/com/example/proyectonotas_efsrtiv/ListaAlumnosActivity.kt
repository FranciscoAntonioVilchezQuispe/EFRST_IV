package com.example.proyectonotas_efsrtiv

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectonotas_efsrtiv.database.DBHelper

class ListaAlumnosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlumnoAdapter
    private lateinit var editTextBuscar: EditText
    private var listaCompleta: List<Alumno> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_alumnos)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewAlumnos)
        editTextBuscar = findViewById(R.id.editTextBuscar)

        listaCompleta = dbHelper.getAllAlumnos()

        adapter = AlumnoAdapter(listaCompleta) { alumno ->
            Toast.makeText(this, "Agregar notas a ${alumno.nombres}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AgregarNotaActivity::class.java)
            intent.putExtra("documento", alumno.documento) // Enviamos el ID
            val mediralumno = alumno.documento
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Buscar por nombre
        editTextBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val texto = s.toString().lowercase()
                val filtrados = listaCompleta.filter {
                    it.nombres.lowercase().contains(texto)
                }
                adapter.actualizarLista(filtrados)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}

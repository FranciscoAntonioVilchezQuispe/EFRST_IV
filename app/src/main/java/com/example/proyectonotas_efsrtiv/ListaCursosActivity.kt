package com.example.proyectonotas_efsrtiv

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectonotas_efsrtiv.database.DBHelper

class ListaCursosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_cursos)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewCursos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cursos = dbHelper.obtenerListaCursosConNotas()

        val adapter = CursoAdapter(cursos) { curso ->
            val intent = Intent(this, AgregarNotaActivity::class.java)
            intent.putExtra("documento", curso.documento)
            intent.putExtra("curso", curso.curso)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}


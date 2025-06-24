package com.example.proyectonotas_efsrtiv

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.graphics.Color
import android.widget.Button


class CursoAdapter(
    private val listaCursos: List<CursoConNotas>,
    private val onEditarClick: (CursoConNotas) -> Unit
) : RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {

    inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreView: TextView = itemView.findViewById(R.id.textNombre)
        val cursoView: TextView = itemView.findViewById(R.id.textCurso)
        val t1View: TextView = itemView.findViewById(R.id.textT1)
        val t2View: TextView = itemView.findViewById(R.id.textT2)
        val finalView: TextView = itemView.findViewById(R.id.textFinal)
        val promView: TextView = itemView.findViewById(R.id.textPromedio)
        val btnEditar: Button = itemView.findViewById(R.id.btnEditar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_curso, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = listaCursos[position]

        holder.nombreView.text = curso.nombreAlumno
        holder.cursoView.text = curso.curso
        holder.t1View.text = "T1: ${curso.t1 ?: "-"}"
        holder.t2View.text = "T2: ${curso.t2 ?: "-"}"
        holder.finalView.text = "Final: ${curso.final ?: "-"}"
        holder.promView.text = "Promedio: ${curso.promedio?.let { "%.2f".format(it) } ?: "-"}"

        // Pintar en rojo si hay nota
        fun pintarNota(view: TextView, nota: Int?) {
            view.setTextColor(if (nota != null) android.graphics.Color.RED else android.graphics.Color.BLACK)
        }
        pintarNota(holder.t1View, curso.t1)
        pintarNota(holder.t2View, curso.t2)
        pintarNota(holder.finalView, curso.final)
        holder.promView.setTextColor(
            if (curso.promedio != null) android.graphics.Color.RED else android.graphics.Color.BLACK
        )

        // Lógica del botón editar
        holder.btnEditar.setOnClickListener {
            onEditarClick(curso)
        }
    }

    override fun getItemCount(): Int = listaCursos.size
}

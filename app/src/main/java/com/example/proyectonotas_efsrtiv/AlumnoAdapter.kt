package com.example.proyectonotas_efsrtiv



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectonotas_efsrtiv.Alumno
import com.example.proyectonotas_efsrtiv.R

class AlumnoAdapter(
    private var listaAlumnos: List<Alumno>,
    private val onAgregarNotasClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    inner class AlumnoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreCompleto: TextView = itemView.findViewById(R.id.textViewNombreCompleto)
        val botonAgregarNotas: Button = itemView.findViewById(R.id.btnAgregarNotas)
        val codigo: TextView = itemView.findViewById(R.id.textViewCodigo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alumno, parent, false)
        return AlumnoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        val alumno = listaAlumnos[position]
        holder.nombreCompleto.text = "${alumno.nombres} ${alumno.apellidop} ${alumno.apellidom}"
        holder.codigo.text = "Código: ${alumno.documento}"
        holder.botonAgregarNotas.setOnClickListener {
            onAgregarNotasClick(alumno)
        }
    }

    override fun getItemCount(): Int = listaAlumnos.size


    fun actualizarLista(nuevaLista: List<Alumno>) {
        listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}

package com.example.proyectonotas_efsrtiv

data class CursoConNotas(
    val documento: String,
    val nombreAlumno: String,
    val curso: String,
    val t1: Int?,
    val t2: Int?,
    val final: Int?,
    val promedio: Double? = 0.0
)

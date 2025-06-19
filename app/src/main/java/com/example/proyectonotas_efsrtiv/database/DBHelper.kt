package com.example.proyectonotas_efsrtiv.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DBHelper (context:Context): SQLiteOpenHelper(context, "NotasEstudiantes.db", null, 1){

    private val context = context

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE Alumno (" +
                    "documento TEXT PRIMARY KEY, " +
                    "nombres TEXT, " +
                    "apellidop TEXT, " +
                    "apellidom TEXT, " +
                    "carrera TEXT, " +
                    "contrasena TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Alumno")
        onCreate(db)
    }

    fun insertarAlumno(
        documento: String,
        nombres: String,
        apellidop: String,
        apellidom: String,
        carrera: String,
        contrasena: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("documento", documento)
            put("nombres", nombres)
            put("apellidop", apellidop)
            put("apellidom", apellidom)
            put("carrera", carrera)
            put("contrasena", contrasena)
        }

        val result = db.insert("Alumno", null, values)
        db.close()

        return result != -1L
    }

    fun validarLogin(documento: String, contrasena: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM Alumno WHERE documento = ? AND contrasena = ?"
        val cursor = db.rawQuery(query, arrayOf(documento, contrasena))
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }

}
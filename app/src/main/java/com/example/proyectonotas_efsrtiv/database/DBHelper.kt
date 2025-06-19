package com.example.proyectonotas_efsrtiv.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectonotas_efsrtiv.Alumno

class DBHelper(context: Context) : SQLiteOpenHelper(context, "NotasEstudiantes.db", null, 1) {

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
        alumno: Alumno
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("documento", alumno.documento)
            put("nombres", alumno.nombres)
            put("apellidop", alumno.apellidop)
            put("apellidom", alumno.apellidom)
            put("carrera", alumno.carrera)
            put("contrasena", alumno.contrasena)
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

    fun getAllAlumnos(): List<Alumno> {
        var arrAlumno = mutableListOf<Alumno>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Alumno", null)
        if (cursor.moveToFirst()) {
            do {
                val obj = Alumno().apply {
                    documento = cursor.getString(cursor.getColumnIndexOrThrow("documento"))
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                    apellidop = cursor.getString(cursor.getColumnIndexOrThrow("apellidop"))
                    apellidom = cursor.getString(cursor.getColumnIndexOrThrow("apellidom"))
                    carrera = cursor.getString(cursor.getColumnIndexOrThrow("carrera"))
                    contrasena = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
                }
                arrAlumno.add(obj)
            } while (cursor.moveToNext())

        }
        cursor.close()
        db.close()
        return arrAlumno
    }
    fun getAlumnoByDocumento(cadena: String): Alumno? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Alumno WHERE documento = ?", arrayOf(cadena))
        var objAlumno: Alumno? = null

        if (cursor.moveToFirst()) {
          val  obj = Alumno().apply {
                documento = cursor.getString(cursor.getColumnIndexOrThrow("documento"))
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                apellidop = cursor.getString(cursor.getColumnIndexOrThrow("apellidop"))
                apellidom = cursor.getString(cursor.getColumnIndexOrThrow("apellidom"))
                carrera = cursor.getString(cursor.getColumnIndexOrThrow("carrera"))
                contrasena = cursor.getString(cursor.getColumnIndexOrThrow("contrasena"))
            }
            objAlumno=obj
        }

        cursor.close()
        db.close()
        return objAlumno
    }
}
package com.example.proyectonotas_efsrtiv.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectonotas_efsrtiv.Alumno
import com.example.proyectonotas_efsrtiv.CursoConNotas

class DBHelper(context: Context) : SQLiteOpenHelper(context, "NotasEstudiantes.db", null, 2) {

    private val context = context

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
        CREATE TABLE Alumno (
            documento TEXT PRIMARY KEY,
            nombres TEXT,
            apellidop TEXT,
            apellidom TEXT,
            carrera TEXT,
            contrasena TEXT
        )
        """.trimIndent()
        )

        db.execSQL(
            """
        CREATE TABLE Notas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            documento TEXT,
            curso TEXT,
            T1 INTEGER,
            T2 INTEGER,
            Final INTEGER,
            promedio REAL,
            UNIQUE(documento, curso)
        )
        """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Alumno")
        db.execSQL("DROP TABLE IF EXISTS Notas")
        onCreate(db)
    }

    fun insertarAlumno(alumno: Alumno): Boolean {
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

    fun registrarNotaParcial(documento: String, curso: String, parcial: String, nota: Int): Boolean {
        val db = this.writableDatabase

        val cursor = db.rawQuery(
            "SELECT T1, T2, Final FROM Notas WHERE documento = ? AND curso = ?",
            arrayOf(documento, curso)
        )

        var t1: Int? = null
        var t2: Int? = null
        var final: Int? = null

        var existe = false

        if (cursor.moveToFirst()) {
            existe = true
            t1 = if (!cursor.isNull(0)) cursor.getInt(0) else null
            t2 = if (!cursor.isNull(1)) cursor.getInt(1) else null
            final = if (!cursor.isNull(2)) cursor.getInt(2) else null
        }

        cursor.close()

        when (parcial) {
            "T1" -> t1 = nota
            "T2" -> t2 = nota
            "Final" -> final = nota
            else -> return false
        }

        val promedio = ((t1 ?: 0) * 0.25f) + ((t2 ?: 0) * 0.25f) + ((final ?: 0) * 0.5f)

        val values = ContentValues().apply {
            put("documento", documento)
            put("curso", curso)
            put("T1", t1)
            put("T2", t2)
            put("Final", final)
            put("promedio", promedio)
        }

        val result = if (existe) {
            db.update("Notas", values, "documento=? AND curso=?", arrayOf(documento, curso))
        } else {
            db.insert("Notas", null, values).toInt()
        }

        db.close()
        return result != -1
    }
    fun obtenerPromedio(documento: String, curso: String): Float {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT promedio FROM Notas WHERE documento = ? AND curso = ?",
            arrayOf(documento, curso)
        )
        val promedio = if (cursor.moveToFirst()) {
            cursor.getFloat(0)
        } else {
            0f
        }
        cursor.close()
        db.close()
        return promedio
    }

    fun obtenerListaCursosConNotas(): List<CursoConNotas> {
        val lista = mutableListOf<CursoConNotas>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT DISTINCT documento, curso FROM Notas", null)

        while (cursor.moveToNext()) {
            val documento = cursor.getString(0)
            val curso = cursor.getString(1)
            val alumno = getAlumnoByDocumento(documento)

            val t1 = obtenerNotaParcial(documento, curso, "T1")
            val t2 = obtenerNotaParcial(documento, curso, "T2")
            val final = obtenerNotaParcial(documento, curso, "Final")
            val promedio = obtenerPromedio(documento, curso)?.toDouble()

            val nombre = if (alumno != null)
                "${alumno.nombres} ${alumno.apellidop} ${alumno.apellidom}" else "Desconocido"

            lista.add(CursoConNotas(
                documento = documento,
                curso = curso,
                t1 = t1,
                t2 = t2,
                final = final,
                promedio = promedio,
                nombreAlumno = nombre
            ))
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerNotaParcial(documento: String, curso: String, parcial: String): Int? {
        val db = readableDatabase

        val columna = when (parcial) {
            "T1" -> "T1"
            "T2" -> "T2"
            "Final" -> "Final"
            else -> return null
        }

        val cursor = db.rawQuery(
            "SELECT $columna FROM Notas WHERE documento = ? AND curso = ?",
            arrayOf(documento, curso)
        )

        val nota = if (cursor.moveToFirst() && !cursor.isNull(0)) cursor.getInt(0) else null
        cursor.close()
        db.close()
        return nota
    }




}
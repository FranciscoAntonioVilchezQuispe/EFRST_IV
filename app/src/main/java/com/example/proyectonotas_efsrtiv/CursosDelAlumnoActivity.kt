package com.example.proyectonotas_efsrtiv

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.example.proyectonotas_efsrtiv.database.DBHelper

class CursosDelAlumnoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var textNombreAlumno: TextView
    private lateinit var btnExportarPDF: Button
    private var documento: String? = null
    private var cursos: List<CursoConNotas> = emptyList()
    private var alumno: Alumno? = null

    companion object {
        private const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1
        private const val REQUEST_PERMISSION_MANAGE_EXTERNAL_STORAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cursos_alumno)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewCursosAlumno)
        textNombreAlumno = findViewById(R.id.textNombreAlumno)
        btnExportarPDF = findViewById(R.id.btnExportarPDF)
        recyclerView.layoutManager = LinearLayoutManager(this)

        documento = intent.getStringExtra("documento")
        if (documento == null) {
            Toast.makeText(this, "Documento no recibido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        alumno = dbHelper.getAlumnoByDocumento(documento!!)
        if (alumno != null) {
            textNombreAlumno.text = "Alumno: ${alumno!!.nombres} ${alumno!!.apellidop} ${alumno!!.apellidom}"
        }

        cursos = dbHelper.obtenerListaCursosConNotas().filter { it.documento == documento }
        recyclerView.adapter = CursoAdapter(cursos) {} // sin botón de editar para el alumno

        btnExportarPDF.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 11+ (API 30+)
                if (Environment.isExternalStorageManager()) {
                    exportarAPDF()
                } else {
                    // Para Android 11+, usar almacenamiento interno o MediaStore
                    exportarAPDFConMediaStore()
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6.0 - 10 (API 23-29)
                if (checkPermission()) {
                    exportarAPDF()
                } else {
                    requestPermission()
                }
            } else {
                // Android < 6.0
                exportarAPDF()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Toast.makeText(this, "Para Android 11+, el PDF se guardará en el almacenamiento de la app", Toast.LENGTH_LONG).show()
            exportarAPDFConMediaStore()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportarAPDF()
                } else {
                    Toast.makeText(this, "Permiso denegado para escribir archivos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun exportarAPDF() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamaño A4
        val page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Configurar estilos de texto
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }

        var yPosition = 80f
        val leftMargin = 50f
        val lineSpacing = 25f

        // Título del documento
        canvas.drawText("REPORTE DE NOTAS", leftMargin, yPosition, titlePaint)
        yPosition += lineSpacing * 2

        // Información del alumno
        alumno?.let {
            canvas.drawText("DATOS DEL ALUMNO", leftMargin, yPosition, headerPaint)
            yPosition += lineSpacing
            canvas.drawText("Documento: ${it.documento}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Nombres: ${it.nombres}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Apellidos: ${it.apellidop} ${it.apellidom}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing * 2
        }

        // Fecha de generación
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Fecha de generación: ${dateFormat.format(Date())}", leftMargin, yPosition, normalPaint)
        yPosition += lineSpacing * 2

        // Cursos y notas
        canvas.drawText("CURSOS Y NOTAS", leftMargin, yPosition, headerPaint)
        yPosition += lineSpacing

        if (cursos.isEmpty()) {
            canvas.drawText("No hay cursos registrados", leftMargin, yPosition, normalPaint)
        } else {
            // Encabezados de tabla
            canvas.drawText("CURSO", leftMargin, yPosition, headerPaint)
            canvas.drawText("NOTA", leftMargin + 300, yPosition, headerPaint)
            canvas.drawText("ESTADO", leftMargin + 400, yPosition, headerPaint)
            yPosition += lineSpacing

            // Línea separadora
            canvas.drawLine(leftMargin, yPosition - 10, 545f, yPosition - 10, normalPaint)
            yPosition += 10

            // Datos de cursos
            for (curso in cursos) {
                if (yPosition > 750) { // Si se acerca al final de la página
                    pdfDocument.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 2).create()
                    val newPage = pdfDocument.startPage(newPageInfo)
                    canvas = newPage.canvas
                    yPosition = 80f
                }

                canvas.drawText(curso.nombreAlumno, leftMargin, yPosition, normalPaint)
                canvas.drawText(curso.promedio.toString(), leftMargin + 300, yPosition, normalPaint)

                val estado = if (curso.promedio!! >= 11) "APROBADO" else "DESAPROBADO"
                val estadoPaint = Paint().apply {
                    color = if (curso.promedio >= 11) Color.GREEN else Color.RED
                    textSize = 14f
                }
                canvas.drawText(estado, leftMargin + 400, yPosition, estadoPaint)
                yPosition += lineSpacing
            }

            // Estadísticas
            yPosition += lineSpacing
            var valor:Double=0.0
            cursos.forEach {it-> valor=valor+ it.promedio!! }
            val promedio = if (cursos.isNotEmpty()) {

                valor/  cursos.size
            } else {
                0.0
            }
            val aprobados = cursos.count { it.promedio!! >= 11 }
            val desaprobados = cursos.size - aprobados

            canvas.drawText("RESUMEN", leftMargin, yPosition, headerPaint)
            yPosition += lineSpacing
            canvas.drawText("Total de cursos: ${cursos.size}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Promedio general: ${"%.2f".format(promedio)}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Cursos aprobados: $aprobados", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Cursos desaprobados: $desaprobados", leftMargin, yPosition, normalPaint)
        }

        pdfDocument.finishPage(page)

        // Guardar el PDF
        try {
            val fileName = "notas_${alumno?.documento}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - usar almacenamiento interno de la app
                val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
                fileOutputStream.close()
                pdfDocument.close()

                Toast.makeText(this, "PDF generado exitosamente en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            } else {
                // Android < 10 - usar Downloads tradicional
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)

                val fileOutputStream = FileOutputStream(file)
                pdfDocument.writeTo(fileOutputStream)
                fileOutputStream.close()
                pdfDocument.close()

                Toast.makeText(this, "PDF generado exitosamente en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al generar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exportarAPDFConMediaStore() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // [Mismo código de generación del PDF que ya tienes...]
        // Configurar estilos de texto
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
        }

        var yPosition = 80f
        val leftMargin = 50f
        val lineSpacing = 25f

        // Título del documento
        canvas.drawText("REPORTE DE NOTAS", leftMargin, yPosition, titlePaint)
        yPosition += lineSpacing * 2

        // Información del alumno
        alumno?.let {
            canvas.drawText("DATOS DEL ALUMNO", leftMargin, yPosition, headerPaint)
            yPosition += lineSpacing
            canvas.drawText("Documento: ${it.documento}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Nombres: ${it.nombres}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Apellidos: ${it.apellidop} ${it.apellidom}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing * 2
        }

        // Fecha de generación
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        canvas.drawText("Fecha de generación: ${dateFormat.format(Date())}", leftMargin, yPosition, normalPaint)
        yPosition += lineSpacing * 2

        // Cursos y notas
        canvas.drawText("CURSOS Y NOTAS", leftMargin, yPosition, headerPaint)
        yPosition += lineSpacing

        if (cursos.isEmpty()) {
            canvas.drawText("No hay cursos registrados", leftMargin, yPosition, normalPaint)
        } else {
            // Encabezados de tabla
            canvas.drawText("CURSO", leftMargin, yPosition, headerPaint)
            canvas.drawText("NOTA", leftMargin + 300, yPosition, headerPaint)
            canvas.drawText("ESTADO", leftMargin + 400, yPosition, headerPaint)
            yPosition += lineSpacing

            // Línea separadora
            canvas.drawLine(leftMargin, yPosition - 10, 545f, yPosition - 10, normalPaint)
            yPosition += 10

            // Datos de cursos
            for (curso in cursos) {
                if (yPosition > 750) { // Si se acerca al final de la página
                    pdfDocument.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 2).create()
                    val newPage = pdfDocument.startPage(newPageInfo)
                    canvas = newPage.canvas
                    yPosition = 80f
                }

                canvas.drawText(curso.nombreAlumno, leftMargin, yPosition, normalPaint)
                canvas.drawText(curso.promedio.toString(), leftMargin + 300, yPosition, normalPaint)

                val estado = if (curso.promedio!! >= 11) "APROBADO" else "DESAPROBADO"
                val estadoPaint = Paint().apply {
                    color = if (curso.promedio >= 11) Color.GREEN else Color.RED
                    textSize = 14f
                }
                canvas.drawText(estado, leftMargin + 400, yPosition, estadoPaint)
                yPosition += lineSpacing
            }

            // Estadísticas
            yPosition += lineSpacing
            var valor:Double=0.0
            cursos.forEach {it-> valor=valor+ it.promedio!! }
            val promedio = if (cursos.isNotEmpty()) {
                valor / cursos.size
            } else {
                0.0
            }
            val aprobados = cursos.count { it.promedio!! >= 11 }
            val desaprobados = cursos.size - aprobados

            canvas.drawText("RESUMEN", leftMargin, yPosition, headerPaint)
            yPosition += lineSpacing
            canvas.drawText("Total de cursos: ${cursos.size}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Promedio general: ${"%.2f".format(promedio)}", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Cursos aprobados: $aprobados", leftMargin, yPosition, normalPaint)
            yPosition += lineSpacing
            canvas.drawText("Cursos desaprobados: $desaprobados", leftMargin, yPosition, normalPaint)
        }

        pdfDocument.finishPage(page)

        // Guardar usando MediaStore (Android 10+)
        try {
            val fileName = "notas_${alumno?.documento}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    val outputStream = resolver.openOutputStream(it)
                    outputStream?.let { stream ->
                        pdfDocument.writeTo(stream)
                        stream.close()
                        pdfDocument.close()
                        Toast.makeText(this, "PDF generado exitosamente en Downloads", Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    // Fallback al almacenamiento interno de la app
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                    val fileOutputStream = FileOutputStream(file)
                    pdfDocument.writeTo(fileOutputStream)
                    fileOutputStream.close()
                    pdfDocument.close()
                    Toast.makeText(this, "PDF generado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al generar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

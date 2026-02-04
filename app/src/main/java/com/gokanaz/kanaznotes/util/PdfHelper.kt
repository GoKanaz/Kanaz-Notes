package com.gokanaz.kanaznotes.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object PdfHelper {
    fun createPdfFromNote(
        context: Context,
        title: String,
        content: String,
        timestamp: Long
    ): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText(title.ifBlank { "Untitled" }, 40f, 50f, paint)
            
            paint.textSize = 12f
            paint.isFakeBoldText = false
            val dateText = "Created: ${java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(java.util.Date(timestamp))}"
            canvas.drawText(dateText, 40f, 80f, paint)
            
            paint.textSize = 14f
            var yPosition = 120f
            val lines = content.split("\n")
            for (line in lines) {
                if (yPosition > 800) break
                canvas.drawText(line, 40f, yPosition, paint)
                yPosition += 20f
            }
            
            pdfDocument.finishPage(page)
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "Note_${System.currentTimeMillis()}.pdf"
            val file = File(downloadsDir, fileName)
            
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

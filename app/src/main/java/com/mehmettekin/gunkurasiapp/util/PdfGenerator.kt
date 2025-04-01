package com.mehmettekin.gunkurasiapp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table


import  com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.mehmettekin.gunkurasiapp.R
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PdfGenerator(private val context: Context) {

    fun generateDrawResultsPdf(results: List<DrawResult>): ResultState<Uri> {
        return try {
            val fileName = "gun_kurasi_sonuclari_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))}.pdf"
            val pdfFile = File(context.filesDir, fileName)

            FileOutputStream(pdfFile).use { outputStream ->
                val pdfWriter = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(pdfWriter)
                val document = Document(pdfDocument)

                // Add title
                val title = Paragraph(context.getString(R.string.draw_results_title))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18f)
                    .setBold()
                document.add(title)

                // Add date
                val date = Paragraph(context.getString(R.string.generated_date, LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10f)
                    .setItalic()
                document.add(date)

                document.add(Paragraph("\n"))

                // Create table
                val table = Table(UnitValue.createPercentArray(floatArrayOf(20f, 45f, 35f)))
                    .setWidth(UnitValue.createPercentValue(100f))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)

                // Add header
                table.addHeaderCell(createCell(context.getString(R.string.month), true))
                table.addHeaderCell(createCell(context.getString(R.string.participant_name), true))
                table.addHeaderCell(createCell(context.getString(R.string.amount), true))

                // Add data rows
                results.forEach { result ->
                    table.addCell(createCell(result.month))
                    table.addCell(createCell(result.participantName))
                    table.addCell(createCell(result.amount))
                }

                document.add(table)

                document.add(Paragraph("\n\n"))

                // Add footer
                val footer = Paragraph(context.getString(R.string.pdf_footer_text))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(9f)
                    .setItalic()
                document.add(footer)

                document.close()
            }

            // Generate content URI for the file
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            ResultState.Success(contentUri)
        } catch (e: Exception) {
            ResultState.Error(UiText.dynamicString("PDF oluşturma hatası: ${e.message}"))
        }
    }

    fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Handle case where no PDF viewer is installed
            val errorIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, context.getString(R.string.no_pdf_viewer_installed))
            }
            context.startActivity(Intent.createChooser(errorIntent, context.getString(R.string.share_via)))
        }
    }

    private fun createCell(text: String, isHeader: Boolean = false): Cell {
        return Cell().apply {
            add(Paragraph(text))
            setTextAlignment(TextAlignment.CENTER)
            if (isHeader) {
                setBackgroundColor(ColorConstants.LIGHT_GRAY)
                setBold()
            }
        }
    }
}
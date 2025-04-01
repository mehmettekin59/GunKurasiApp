package com.mehmettekin.gunkurasiapp.domain.usecase

import android.content.Context
import android.net.Uri
import com.mehmettekin.gunkurasiapp.util.PdfGenerator
import javax.inject.Inject

class OpenPdfUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(uri: Uri) {
        val pdfGenerator = PdfGenerator(context)
        pdfGenerator.openPdf(uri)
    }
}
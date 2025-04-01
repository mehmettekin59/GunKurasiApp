package com.mehmettekin.gunkurasiapp.domain.usecase

import android.content.Context
import android.net.Uri
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.util.PdfGenerator
import com.mehmettekin.gunkurasiapp.util.ResultState
import javax.inject.Inject

class GeneratePdfUseCase @Inject constructor(
    private val context: Context
) {
    operator fun invoke(results: List<DrawResult>): ResultState<Uri> {
        val pdfGenerator = PdfGenerator(context)
        return pdfGenerator.generateDrawResultsPdf(results)
    }
}
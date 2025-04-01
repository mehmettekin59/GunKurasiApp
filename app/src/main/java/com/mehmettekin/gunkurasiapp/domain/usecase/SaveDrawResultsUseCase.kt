package com.mehmettekin.gunkurasiapp.domain.usecase

import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.repository.DrawRepository
import com.mehmettekin.gunkurasiapp.util.ResultState
import javax.inject.Inject

class SaveDrawResultsUseCase @Inject constructor(
    private val drawRepository: DrawRepository
) {
    suspend operator fun invoke(results: List<DrawResult>): ResultState<Unit> {
        return drawRepository.saveDrawResults(results)
    }
}
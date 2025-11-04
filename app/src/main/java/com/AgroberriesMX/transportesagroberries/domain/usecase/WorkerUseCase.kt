package com.AgroberriesMX.transportesagroberries.domain.usecase

import com.AgroberriesMX.transportesagroberries.domain.Repository
import javax.inject.Inject

class WorkerUseCase @Inject constructor(private val repository: Repository) {
    //suspend operator fun invoke(token: String) = repository.getWorkers(token)
    suspend operator fun invoke() = repository.getWorkers()
}
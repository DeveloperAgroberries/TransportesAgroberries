package com.AgroberriesMX.transportesagroberries.domain.usecase

import com.AgroberriesMX.transportesagroberries.domain.Repository
import javax.inject.Inject

class LoginsUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(token: String) = repository.getLogins(token)
}
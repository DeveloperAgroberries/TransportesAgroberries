package com.AgroberriesMX.transportesagroberries.domain.usecase

import com.AgroberriesMX.transportesagroberries.domain.Repository
import javax.inject.Inject

class VehicleUseCase @Inject constructor(private val repository: Repository) {
    //suspend operator fun invoke(token: String) = repository.getVehicles(token)
    suspend operator fun invoke() = repository.getVehicles()
}
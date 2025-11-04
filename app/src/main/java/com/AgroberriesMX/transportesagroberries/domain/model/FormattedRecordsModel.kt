package com.AgroberriesMX.transportesagroberries.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FormattedRecordsModel(
    val cCodigoappTrn: Int, // Asumimos String, ya que suele ser un UUID
    val vChoferTrn: String,
    val cCodigoTra: String,
    val cFormaregTrn: String,
    val dRegistroTrn: String,
    val cTiporegTrn: String,
    val cLongitudTrn: String,
    val cLatitudTrn: String,
    val cAlturaTrn: String,
    val cCodigoUsu: String,
    val dCreacionTrn: String,
    val cControlVeh: Int,
    val cControlRut: Int,
    val nCostoRut: Double, // Usamos Double para costos, por si tienen decimales
    val cUsumodTrn: String?,
    val dModifiTrn: String?
) : Parcelable
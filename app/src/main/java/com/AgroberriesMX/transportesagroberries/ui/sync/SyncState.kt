package com.AgroberriesMX.transportesagroberries.ui.sync

sealed class SyncState {
    object Waiting : SyncState()
    object Loading : SyncState()
    object CatalogSuccess : SyncState() // Add this line
    data class UploadSuccess(val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
}
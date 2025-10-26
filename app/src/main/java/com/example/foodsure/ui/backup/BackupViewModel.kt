package com.example.foodsure.ui.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.foodsure.data.ModelRepository
import com.example.foodsure.ui.BaseViewModel
import de.raphaelebner.roomdatabasebackup.core.RoomBackup
import kotlinx.coroutines.launch

class BackupViewModel(r: ModelRepository) : BaseViewModel(r) {
    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean> = _navigateBack

    private val _toastMsg = MutableLiveData<String>("")
    val toastMsg: LiveData<String> = _toastMsg

    private val _restartApp = MutableLiveData<Boolean>()
    val restartApp: LiveData<Boolean> = _restartApp

    fun setToast(string: String) {
        _toastMsg.value = string
    }

    fun onDoneNavigation() {
        _navigateBack.value = false
    }

    fun onDoneToast() {
        _toastMsg.value = ""
    }

    fun onDoneRestartApp() {
        _restartApp.value = false
    }

    fun uploadBackup(backup: RoomBackup) = viewModelScope.launch {
        val response = repository.uploadBackup(backup)
        when (response) {
            ModelRepository.RespondStatus.SUCCESS -> {
                _toastMsg.value = "Backup Uploaded"
            }

            ModelRepository.RespondStatus.FAIL_ON_BACKUP -> {
                _toastMsg.value = "Backup Failed"
            }

            ModelRepository.RespondStatus.FAIL_ON_UPLOAD -> {
                _toastMsg.value = "Backup Upload Failed"
            }

            ModelRepository.RespondStatus.FAIL_ON_DELETE -> {
                _toastMsg.value = "Backup Delete Failed"
            }

            else -> {
                _toastMsg.value = "Unknown Failed"
            }
        }
    }

    fun restoreBackup(backup: RoomBackup) = viewModelScope.launch {
        val response = repository.downloadBackup(backup)
        when (response) {
            ModelRepository.RespondStatus.SUCCESS -> {
                _toastMsg.value = "Backup Downloaded"
                _restartApp.value = true
            }

            ModelRepository.RespondStatus.FAIL_ON_DOWNLOAD -> {
                _toastMsg.value = "Backup Download Failed"
            }

            ModelRepository.RespondStatus.FAIL_ON_RESTORE -> {
                _toastMsg.value = "Backup Restore Failed"
            }

            else -> {
                _toastMsg.value = "Unknown Failed"
            }
        }
    }
}

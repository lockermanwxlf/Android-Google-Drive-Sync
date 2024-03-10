package com.lockermanwxlf.drivesync.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.lockermanwxlf.drivesync.utils.data.Sync
import com.lockermanwxlf.drivesync.utils.data.SyncDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val syncDao: SyncDao
): ViewModel() {
    var syncs by mutableStateOf(emptyFlow<List<Sync>>())
        private set

    fun setAccount(account: GoogleSignInAccount?) {
        syncs = account?.let { syncDao.getSyncFlow(account.id!!) }?: emptyFlow()
    }

    fun deleteSync(sync: Sync) = viewModelScope.launch(Dispatchers.IO) {
        syncDao.delete(sync)
    }
}
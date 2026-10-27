package com.devpupu.notifgrouper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devpupu.notifgrouper.data.db.NotificationHistoryDao
import com.devpupu.notifgrouper.data.db.NotificationHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(dao: NotificationHistoryDao) : ViewModel() {
    val history: StateFlow<List<NotificationHistoryEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
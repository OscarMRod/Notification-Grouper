package com.devpupu.notifgrouper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devpupu.notifgrouper.data.repository.AppPreferencesRepository
import com.devpupu.notifgrouper.domain.model.GroupedApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: AppPreferencesRepository
) : ViewModel() {
    val apps: StateFlow<List<GroupedApp>> = repo.groupedApps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggle(packageName: String, enabled: Boolean) {
        viewModelScope.launch { repo.toggle(packageName, enabled) }
    }
}
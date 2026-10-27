package com.devpupu.notifgrouper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devpupu.notifgrouper.data.db.GroupRuleDao
import com.devpupu.notifgrouper.data.db.GroupRuleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(private val dao: GroupRuleDao) : ViewModel() {
    val rules: StateFlow<List<GroupRuleEntity>> = dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun add(name: String, keyword: String, groupKey: String) = viewModelScope.launch {
        dao.insert(GroupRuleEntity(name = name, keyword = keyword, groupKey = groupKey))
    }
    fun remove(rule: GroupRuleEntity) = viewModelScope.launch { dao.delete(rule) }
}
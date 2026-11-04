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
    companion object {
        const val MAX_NAME = 64
        const val MAX_KEYWORD = 128
        const val MAX_GROUP_KEY = 64
    }

    fun add(name: String, keyword: String, groupKey: String) = viewModelScope.launch {
        val n = name.trim().take(MAX_NAME)
        val k = keyword.trim().take(MAX_KEYWORD)
        val g = groupKey.trim().take(MAX_GROUP_KEY)
        if (n.isBlank() || k.isBlank() || g.isBlank()) return@launch
        dao.insert(GroupRuleEntity(name = n, keyword = k, groupKey = g))
    }
    fun remove(rule: GroupRuleEntity) = viewModelScope.launch { dao.delete(rule) }
}
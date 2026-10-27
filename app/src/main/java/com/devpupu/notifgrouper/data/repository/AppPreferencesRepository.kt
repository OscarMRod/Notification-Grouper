package com.devpupu.notifgrouper.data.repository

import com.devpupu.notifgrouper.data.db.GroupedAppDao
import com.devpupu.notifgrouper.data.db.GroupedAppEntity
import com.devpupu.notifgrouper.domain.model.GroupedApp
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AppPreferencesRepository @Inject constructor(
    private val dao: GroupedAppDao
) {
    val groupedApps: Flow<List<GroupedApp>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    fun groupedApps(): Flow<List<GroupedApp>> = groupedApps

    suspend fun isGrouped(packageName: String): Boolean =
        dao.getByPackage(packageName)?.enabled ?: false

    suspend fun setGrouped(packageName: String, appName: String? = null, enabled: Boolean) {
        val existing = dao.getByPackage(packageName)
        if (existing == null) {
            dao.upsert(GroupedAppEntity(packageName = packageName, appName = appName, enabled = enabled))
        } else {
            dao.setEnabled(packageName, enabled)
            if (appName != null && appName != existing.appName) {
                dao.upsert(existing.copy(appName = appName, enabled = enabled))
            }
        }
    }

    suspend fun toggle(packageName: String, enabled: Boolean) {
        val existing = dao.getByPackage(packageName)
        if (existing == null) {
            dao.upsert(GroupedAppEntity(packageName = packageName, enabled = enabled))
        } else {
            dao.setEnabled(packageName, enabled)
        }
    }

    suspend fun registerIfMissing(packageName: String, appName: String? = null) {
        if (dao.getByPackage(packageName) == null) {
            dao.upsert(GroupedAppEntity(packageName = packageName, appName = appName, enabled = false))
        }
    }

    suspend fun remove(packageName: String) {
        dao.delete(packageName)
    }

    private fun GroupedAppEntity.toDomain() = GroupedApp(
        packageName = packageName,
        appName = appName,
        enabled = enabled
    )
}

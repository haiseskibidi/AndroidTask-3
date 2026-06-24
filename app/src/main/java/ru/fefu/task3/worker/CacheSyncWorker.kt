package ru.fefu.task3.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import ru.fefu.task3.domain.model.AnimeRepository

@HiltWorker
class CacheSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: AnimeRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 1. очищаем кэш деталей аниме старше 24 часов (TTL)
            val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            repository.deleteOldCache(oneDayAgo)

            // 2. предзагружаем/обновляем кэш для всех избранных аниме всех пользователей
            val users = repository.getAllUsers().first()
            val favIds = mutableSetOf<Long>()
            
            for (user in users) {
                val favList = repository.getFavouriteAnimes(user.id).first()
                favIds.addAll(favList.map { it.id })
            }

            // фоново обновляем детали каждого избранного аниме
            for (id in favIds) {
                try {
                    repository.forceCacheDetails(id)
                } catch (e: Exception) {
                    // игнорируем ошибки сети для конкретного аниме
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

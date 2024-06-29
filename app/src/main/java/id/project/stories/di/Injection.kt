package id.project.stories.di

import android.content.Context
import id.project.stories.data.local.StoryDatabase
import id.project.stories.data.preferences.UserPreference
import id.project.stories.data.preferences.dataStore
import id.project.stories.data.remote.api.ApiConfig
import id.project.stories.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val database = StoryDatabase.getDatabase(context) // -> local database
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking {
            pref.getSession().first()
        }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(database, pref, apiService)
    }
}
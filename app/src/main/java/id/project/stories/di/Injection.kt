package id.project.stories.di

import android.content.Context
import id.project.stories.data.preferences.UserPreference
import id.project.stories.data.preferences.dataStore
import id.project.stories.data.remote.api.ApiConfig
import id.project.stories.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}
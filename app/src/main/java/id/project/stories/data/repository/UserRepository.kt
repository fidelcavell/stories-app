package id.project.stories.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import id.project.stories.data.preferences.UserModel
import id.project.stories.data.preferences.UserPreference
import id.project.stories.data.remote.api.ApiService
import id.project.stories.data.remote.response.ListStoryItem
import id.project.stories.data.remote.response.LoginResponse
import id.project.stories.data.remote.response.RegisterResponse
import id.project.stories.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    // Preferences :
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    // Api Service :
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name = name, email = email, password = password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email = email, password = password)
    }

    suspend fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): RegisterResponse {
        return apiService.uploadStory(file = file, description = description, lat = lat, lon = lon)
    }

    suspend fun getAllStoriesWithLocation(location: Int): StoryResponse {
        return apiService.getAllStories(location =  location)
    }

    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(apiService = apiService)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
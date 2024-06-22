package id.project.stories.view.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.project.stories.data.remote.response.ListStoryItem
import id.project.stories.data.repository.UserRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    private val _listOfStory = MutableLiveData<List<ListStoryItem>>()
    val listOfStory: LiveData<List<ListStoryItem>> = _listOfStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllStoriesWithLocation(authToken: String, location: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getAllStoriesWithLocation(authToken = authToken, location = location)
                _listOfStory.value = response.listStory

            } catch (e: HttpException) {
                Log.e(TAG, "onFailure: ${e.message()}")
            }
            _isLoading.value = false
        }
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}
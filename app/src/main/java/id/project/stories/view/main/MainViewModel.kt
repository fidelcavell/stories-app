package id.project.stories.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.project.stories.data.local.StoryModel
import id.project.stories.data.preferences.UserModel
import id.project.stories.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    // Preferences :
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    // Api Service :
    fun getAllStories(): LiveData<PagingData<StoryModel>> {
        return repository.getAllStories().cachedIn(viewModelScope)
    }
}
package id.project.stories.view.addStory

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import id.project.stories.data.remote.response.ErrorResponse
import id.project.stories.data.remote.response.RegisterResponse
import id.project.stories.data.repository.UserRepository
import id.project.stories.utils.reduceFileImage
import id.project.stories.utils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uploadStory = MutableLiveData<RegisterResponse>()
    val uploadStory: LiveData<RegisterResponse> = _uploadStory

    private val _errorResponse = MutableLiveData<String?>()
    val errorResponse: LiveData<String?> = _errorResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    @RequiresApi(Build.VERSION_CODES.Q)
    fun uploadStory(authToken: String, image: Uri, description: String, lat: Double? = null, lon: Double? = null, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val imageFile = uriToFile(image, context).reduceFileImage()
                Log.d(TAG, "showImage: ${imageFile.path}")

                val descriptionRequest = description.toRequestBody("text/plain".toMediaType())
                val latRequest = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonRequest = lon?.toString()?.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val response = repository.uploadStory(authToken, multipartBody, descriptionRequest, latRequest, lonRequest)
                _uploadStory.value = response

            } catch (e: HttpException) {
                Log.e(TAG, "onFailure: ${e.message()}")
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                _errorResponse.value = errorBody.message
            }
            _isLoading.value = false
        }
    }

    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}
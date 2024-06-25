package id.project.stories.view.addStory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import id.project.stories.R
import id.project.stories.databinding.ActivityAddStoryBinding
import id.project.stories.utils.ViewModelFactory
import id.project.stories.utils.component.CustomAlertDialog
import id.project.stories.utils.getImageUri
import id.project.stories.view.main.MainActivity

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private var currentImageUri: Uri? = null
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location? = null

    private val customAlertDialog = CustomAlertDialog(this@AddStoryActivity)

    // Access Camera :
    private val launchCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            Log.d("Camera", "Failed to Launch Camera")
            customAlertDialog.apply {
                create(
                    title = "Error",
                    message = "Failed to launch Camera",
                    hasNegativeBtn = false,
                    onPositiveButtonClick = { /* Do Nothing */ },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }
    }

    // Access Gallery :
    private val launchGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No Media Selected")
            customAlertDialog.apply {
                create(
                    title = "Error",
                    message = "No Media Selected",
                    hasNegativeBtn = false,
                    onPositiveButtonClick = { /* Do Nothing */ },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }
    }

    // Access Location Permission (Fine and Coarse Location Access) :
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise Location Permission Access Granted (Fine)
                getLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Approximate Location Permission Access Granted (Coarse)
                getLocation()
            }

            else -> {
                // No Location Permission Access Granted
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            cameraButton.setOnClickListener {
                startCamera()
            }
            galleryButton.setOnClickListener {
                startGallery()
            }
            uploadButton.setOnClickListener {
                uploadStory()
            }
            switchLocation.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    getLocation()
                } else {
                    // To Set Location Null when Switch Button is off :
                    location = null
                }
            }
            backButton.setOnClickListener {
                finish()
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.uploadStory.observe(this) { response ->
            customAlertDialog.apply {
                create(
                    title = "Success",
                    message = response.message,
                    hasNegativeBtn = false,
                    onPositiveButtonClick = {
                        val intentToMain = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intentToMain.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intentToMain)
                        finish()
                    },
                    onNegativeButtonClick = { /* Do Nothing */ }
                )
                show()
            }
        }

        viewModel.errorResponse.observe(this) { errorMessage ->
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("$errorMessage\n\n" + getString(R.string.error_addtional_warning))
                setPositiveButton("Ok") { _, _ ->
                    // Do Nothing
                }
                create()
                show()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
            Log.d("Image URI", "showImage: $it")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launchCamera.launch(currentImageUri!!)
    }

    private fun startGallery() {
        launchGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val description = binding.descriptionEditText.text.toString().trim()
            viewModel.uploadStory(
                image = uri,
                description = description,
                lat = location?.latitude,
                lon = location?.longitude,
                context = this@AddStoryActivity
            )

        } ?: customAlertDialog.apply {
            create(
                title = "Invalid",
                message = getString(R.string.empty_warning),
                hasNegativeBtn = false,
                onPositiveButtonClick = { /* Do Nothing */ },
                onNegativeButtonClick = { /* Do Nothing */ }
            )
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location

                } else {
                    customAlertDialog.apply {
                        create(
                            title = "Invalid",
                            message = getString(R.string.location_not_found_message),
                            hasNegativeBtn = false,
                            onPositiveButtonClick = { /* Do Nothing */ },
                            onNegativeButtonClick = { /* Do Nothing */ }
                        )
                        show()
                    }
                    binding.switchLocation.isChecked = false
                }
            }

        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}
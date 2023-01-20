package com.nandaadisaputra.storyapp.ui.activity.add

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.google.android.material.snackbar.Snackbar
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.activity.BaseActivity
import com.nandaadisaputra.storyapp.data.local.constant.Const
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.databinding.ActivityAddStoryBinding
import com.nandaadisaputra.storyapp.ui.activity.story.MainActivity
import com.nandaadisaputra.storyapp.ui.utils.reduceFileImage
import com.nandaadisaputra.storyapp.ui.utils.rotateBitmap
import com.nandaadisaputra.storyapp.ui.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@AndroidEntryPoint
class AddStoryActivity :
    BaseActivity<ActivityAddStoryBinding, AddStoryViewModel>(R.layout.activity_add_story) {
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                tos(resources.getString(R.string.error_permission))
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActionBar()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnCamera.setOnClickListener(this)
        binding.btnGallery.setOnClickListener(this)
        binding.btnUpload.setOnClickListener(this)
    }

    private fun setActionBar() {
        supportActionBar?.title = Const.TITLE.ADD
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_camera -> startCameraX()
            R.id.btn_gallery -> startGallery()
            R.id.btn_upload -> handleSubmit()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            Glide.with(this)
                .load(selectedImg)
                .centerCrop()
                .fitCenter()
                .placeholder(com.crocodic.core.R.drawable.placeholder_img)
                .error(com.crocodic.core.R.drawable.mtrl_ic_error)
                .into(binding.ivPhoto)
        }
    }

    private fun startCameraX() {
        val cameraActivity = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(cameraActivity)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            getFile = myFile
            Glide.with(this)
                .load(result)
                .centerCrop()
                .fitCenter()
                .placeholder(com.crocodic.core.R.drawable.placeholder_img)
                .error(com.crocodic.core.R.drawable.mtrl_ic_error)
                .into(binding.ivPhoto)
        }
    }

    private fun handleSubmit() {
        val description = binding.edtDescription.textOf()
        val session = LoginPreference(this)
        val token: String = session.getString(session.tokenUser).toString()
        if (validateAllFields(description)) {
            val file = reduceFileImage(getFile as File)
            val descriptionBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.addStory(token, descriptionBody, imageMultipart).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.addProgressBar.visibility = View.VISIBLE
                        binding.btnUpload.isEnabled = false
                    }
                    is Result.Success -> {
                        binding.addProgressBar.visibility = View.GONE
                        binding.btnUpload.isEnabled = true
                        tos(resources.getString(R.string.successful_upload_message))
                        openActivity<MainActivity>()
                        finish()
                    }
                    is Result.Error -> {
                        binding.addProgressBar.visibility = View.GONE
                        binding.btnUpload.isEnabled = true

                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun validateAllFields(description: String): Boolean {
        if (getFile == null) {
            tos(resources.getString(R.string.error_required_photo_message))
            return false
        }

        if (description.isEmpty()) {
            binding.edtDescription.error =
                resources.getString(R.string.error_required_description_message)
            return false
        }

        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
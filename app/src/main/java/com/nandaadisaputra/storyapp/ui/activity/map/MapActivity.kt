package com.nandaadisaputra.storyapp.ui.activity.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.crocodic.core.extension.tos
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.activity.BaseActivity
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.databinding.ActivityMapBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MapActivity : BaseActivity<ActivityMapBinding, MapViewModel>(R.layout.activity_map),
    OnMapReadyCallback {
    @Inject
    lateinit var preferences: LoginPreference

    private lateinit var map: GoogleMap
    private var markers: ArrayList<Marker?> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.map.getFragment<SupportMapFragment>().getMapAsync(this)
        setDatamaps()
    }

    private fun setDatamaps() {
        val session = LoginPreference(this)
        val userToken = preferences.getString(session.tokenUser).toString()
        viewModel.getStoryLocation("Bearer $userToken").observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        loadingDialog.setMessage("loading...")
                    }
                    is Result.Error -> {
                        loadingDialog.setMessage("success...")
                        tos("Failed. Cause: ${result.error}")
                    }
                    is Result.Success -> {
                        loadingDialog.setMessage("error...")
                        val data = result.data
                        setMarker(data)
                        setMapCamera()
                    }
                }
            }
        }
    }


    private fun setMapStyle() {
        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style))
            if (!success) {
                Timber.tag(TAG).e("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Timber.tag(TAG).e(exception, "Can't find style. Error: ")
        }
    }
    private fun setMarker(location: List<StoryEntity>?) {
        location!!.forEach {
            if (it.lat != null && it.lon != null) {
                val name = it.name
                val marker = LatLng(it.lat, it.lon)
                map.addMarker(
                    MarkerOptions()
                        .position(marker)
                        .title("Name : $name")
                        .snippet(
                            it.description + " | " + it.createdAt.toString()
                                .removeRange(16, it.createdAt.toString().length)
                        )
                )
                map.moveCamera(CameraUpdateFactory.newLatLng(marker))
            }
        }
    }
    private fun setMapCamera() {
        val listOfMarker = markers
        val b = LatLngBounds.Builder()
        for (m in listOfMarker) {
            m?.position?.let { b.include(it) }
        }
        val bounds = b.build()
        val paddingFromEdgeAsPX = 100
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, paddingFromEdgeAsPX)
        map.animateCamera(cu)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = true
        map.uiSettings.isIndoorLevelPickerEnabled = true
        map.uiSettings.isCompassEnabled = true
        setMapStyle()
        getMyLocation()
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            requestPermissionLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
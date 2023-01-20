package com.nandaadisaputra.storyapp.ui.fragment.location

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.crocodic.core.extension.tos
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.nandaadisaputra.storyapp.R
import com.nandaadisaputra.storyapp.base.fragment.BaseFragment
import com.nandaadisaputra.storyapp.data.local.preference.LoginPreference
import com.nandaadisaputra.storyapp.data.remote.Result
import com.nandaadisaputra.storyapp.data.remote.story.StoryEntity
import com.nandaadisaputra.storyapp.databinding.FragmentMapBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {
    private lateinit var viewModel: MapViewModel

    @Inject
    lateinit var preferences: LoginPreference

    private lateinit var map: GoogleMap
    private var markers: ArrayList<Marker?> = arrayListOf()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this@MapFragment)[MapViewModel::class.java]
        binding?.map?.getFragment<SupportMapFragment>()?.getMapAsync(this)
        setDatamaps()
    }

    private fun setDatamaps() {
        val session = LoginPreference(requireContext())
        val userToken = preferences.getString(session.tokenUser).toString()
        viewModel.getStoryLocation("Bearer $userToken").observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                    }
                    is Result.Error -> {
                        activity?.tos("Failed. Cause: ${result.error}")
                    }
                    is Result.Success -> {
                        val data = result.data
                        setMarker(data)
                        setMapCamera()
                    }
                }
            }
        }
    }

    private fun  setMarker(location: List<StoryEntity>?) {
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
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style))
            if (!success) {
                Timber.tag(TAG).e("Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Timber.tag(TAG).e(exception, "Can't find style. Error: ")
        }
    }
    companion object {
        private const val TAG = "MapsActivity"
        fun newInstance(): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


}

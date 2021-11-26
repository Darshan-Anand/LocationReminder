package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private val TAG = "SelectLocationFragment"

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var googleMap: GoogleMap
    private lateinit var poi: PointOfInterest


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        return binding.root
    }

    private fun onLocationSelected() {
        binding.saveLocBut.setOnClickListener {
            if (this::poi.isInitialized) {
                Log.d(TAG, "Selected Poi= $poi")
                Log.d(TAG,"Selected Poi_latLng= ${poi.latLng}")
                Log.d(TAG,"Selected Poi_lat= ${poi.latLng.latitude}")
                Log.d(TAG,"Selected Poi_long= ${poi.latLng.longitude}")
                Log.d(TAG,"Selected Poi_name= ${poi.name}")
                _viewModel.latitude.value = poi.latLng.latitude
                _viewModel.longitude.value = poi.latLng.longitude
                _viewModel.selectedPOI.value = poi
                _viewModel.reminderSelectedLocationStr.value = poi.name
                _viewModel.navigationCommand.value = NavigationCommand.Back
            } else {
                Toast.makeText(requireContext(), R.string.select_poi, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.normal_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.hybrid_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            R.id.satellite_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.terrain_map -> {
                googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true
        val bengaluru = LatLng(12.965616, 77.5761)
        val zoomLevel = 12f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bengaluru, zoomLevel))
        setMapStyle(googleMap)
        enableLocation()
        setPoiClickListener(googleMap)
        setOnMapClick(googleMap)
        onLocationSelected()
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setPoiClickListener(map: GoogleMap) {
        map.setOnPoiClickListener {
            map.clear()
            poi = it
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            map.addCircle(
                CircleOptions()
                    .center(poi.latLng)
                    .radius(250.0)
                    .strokeColor(Color.argb(255, 0, 0, 255))
                    .fillColor(Color.argb(64, 0, 0, 255)).strokeWidth(4F)

            )
            poiMarker.showInfoWindow()
        }
    }

    private fun setOnMapClick(map: GoogleMap) {
        map.setOnMapClickListener {
            map.clear()
            val droppedPin = getString(R.string.dropped_pin)
            val snippet = getValueSnippet(it)
            poi = PointOfInterest(it, droppedPin, snippet)
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title(getString(R.string.dropped_pin))
                    .snippet(getValueSnippet(it))
            )
            map.addCircle(
                CircleOptions()
                    .center(it)
                    .radius(250.0)
                    .strokeColor(Color.argb(255, 0, 0, 255))
                    .fillColor(Color.argb(64, 0, 0, 255)).strokeWidth(4F)

            )
            poiMarker.showInfoWindow()
        }
    }

    private fun getValueSnippet(latLng: LatLng) = String.format(
        Locale.getDefault(),
        "Lat: %1$.5f, Long: %2$.5f",
        latLng.latitude,
        latLng.longitude
    )

    private fun enableLocation() {
        when {
            (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {
                googleMap.isMyLocationEnabled = true
            }
            (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )) -> {
                val snackbar = Snackbar.make(
                    binding.root,
                    R.string.to_know_your_location,
                    Snackbar.LENGTH_LONG
                ).show()
                requestPermission()
            }
            else -> {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            FOREGROUND_PERMISSIONS_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FOREGROUND_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    "Location Permission not granted",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

}

const val FOREGROUND_PERMISSIONS_REQUEST_CODE = 34
const val REQUEST_TURN_DEVICE_LOCATION_ON = 29

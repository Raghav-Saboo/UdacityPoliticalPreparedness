package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class RepresentativeFragment : Fragment() {

  private val TAG = RepresentativeFragment::class.java.simpleName
  private val viewModel: RepresentativeViewModel by lazy {
    ViewModelProvider(this,
                      RepresentativeViewModelFactory())[RepresentativeViewModel::class.java]
  }
  private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

  //TODO: Declare ViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentRepresentativeBinding.inflate(inflater)
    binding.viewModel = viewModel
    binding.lifecycleOwner = this
    fusedLocationProviderClient =
      LocationServices.getFusedLocationProviderClient(this.requireActivity())

    //TODO: Establish bindings

    //TODO: Define and assign Representative adapter

    //TODO: Populate Representative adapter

    //TODO: Establish button listeners for field and location search

    val adapter =
      RepresentativeListAdapter(RepresentativeListAdapter.RepresentativeClickListener {})

    binding.representativeRecycler.adapter = adapter

    binding.representativeContainer.setTransition(R.id.start, R.id.start)

    viewModel.representatives.observe(viewLifecycleOwner) {
      if (!it.isNullOrEmpty()) {
        adapter.submitList(it)
        binding.representativeContainer.setTransition(R.id.start, R.id.end)
      } else {
        binding.representativeContainer.setTransition(R.id.start, R.id.start)
      }
    }

    binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setState(requireContext().resources.getStringArray(R.array.states)[position])
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    viewModel.errMsg.observe(viewLifecycleOwner) {
      it?.let {
        showToast(it)
        viewModel.postErrorMsgToast()
      }
    }

    binding.searchButton.setOnClickListener {
      viewModel.searchRepresentatives()
    }

    binding.locationButton.setOnClickListener {
      checkLocationPermissions()
    }

    viewModel.loading.observe(viewLifecycleOwner) {
      if (it) {
        hideKeyboard()
      }
    }

    return binding.root

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // Check if location permissions are granted and if so enable the
    // location data layer.
    if (requestCode == REQUEST_LOCATION_PERMISSION) {
      if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
        Log.d(TAG, "Location access is granted")
        getLocation()
        return
      }
    }
    showToast(this.requireContext().getString(R.string.error_location_permission_denied))
  }

  private fun checkLocationPermissions() {
    return if (isPermissionGranted()) {
      getLocation()
    } else {
      //TODO: Request Location permissions
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                         REQUEST_LOCATION_PERMISSION)
    }
  }

  private fun isPermissionGranted(): Boolean {
    return (ActivityCompat.checkSelfPermission(this.requireContext(),
                                               Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED)
  }

  @SuppressLint("MissingPermission")
  private fun getLocation() {
    val locationResult = fusedLocationProviderClient.lastLocation
    locationResult.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        // Set the map's camera position to the current location of the device.
        val lastKnownLocation = task.result
        Log.i(TAG,
              "Current location is ${lastKnownLocation?.latitude} ${lastKnownLocation?.longitude}")
        try {
          if (lastKnownLocation != null) {
            viewModel.searchRepresentatives(geoCodeLocation(lastKnownLocation))
          }
        } catch (e: Exception) {
          Log.e(TAG, "Exception: %s", e)
          Toast.makeText(this.requireContext(),
                         getString(R.string.error_failed_get_address_from_location),
                         Toast.LENGTH_SHORT).show()
        }
      } else {
        Log.d(TAG, "Current location is null. Using defaults.")
      }
      //TODO: Get location from LocationServices
      //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }
  }

  private fun geoCodeLocation(location: Location): Address {
    val geocoder = Geocoder(context, Locale.getDefault())
    return geocoder.getFromLocation(location.latitude, location.longitude, 1)
      .map { address ->
        Log.i(TAG, "Address: ${address.locale}, ${address.postalCode}")
        Address(address.thoroughfare,
                address.subThoroughfare,
                address.locality,
                address.adminArea,
                address.postalCode)
      }
      .first()
  }

  private fun hideKeyboard() {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view!!.windowToken, 0)
  }

  private fun showToast(text: String) {
    Toast.makeText(this.requireContext(), text, Toast.LENGTH_SHORT)
      .show()
  }

  companion object {
    //TODO: Add Constant for Location request
    const val REQUEST_LOCATION_PERMISSION = 1
  }

}
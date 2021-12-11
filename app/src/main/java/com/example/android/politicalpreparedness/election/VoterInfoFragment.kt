package com.example.android.politicalpreparedness.election

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class VoterInfoFragment : Fragment() {

  private val TAG = VoterInfoFragment::class.java.simpleName
  private val REQUEST_LOCATION_PERMISSION = 1
  private val params: VoterInfoFragmentArgs by navArgs()
  private val viewModel: VoterInfoViewModel by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onViewCreated()"
    }
    requireNotNull(params.argElection) {
      "You can only access after params are initialized"
    }
    requireNotNull(params.argIsSaved) {
      "You can only access after params are initialized"
    }
    ViewModelProvider(this,
                      VoterInfoViewModelFactory(activity.application,
                                                params.argElection,
                                                params.argIsSaved))[VoterInfoViewModel::class.java]
  }
  private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentVoterInfoBinding.inflate(inflater)

    binding.lifecycleOwner = this
    binding.viewModel = viewModel

    binding.dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
//    binding.stateLocations.movementMethod = LinkMovementMethod.getInstance()
//    binding.stateBallot.movementMethod = LinkMovementMethod.getInstance()

    fusedLocationProviderClient =
      LocationServices.getFusedLocationProviderClient(this.requireActivity())

    viewModel.navigateToElectionsList.observe(this) {
      if (it != null) {
        this.findNavController()
          .navigate(VoterInfoFragmentDirections.actionVoterInfoFragmentToElectionsFragment())
        viewModel.displayElectionListComplete()
      }
    }

    viewModel.electionDetails.observe(viewLifecycleOwner) { state ->
      state.let {
        binding.constraintLayout.transitionToEnd()
      }
    }

    binding.stateLocations.setOnClickListener {
      viewModel.electionDetails.value.let { state ->
        if (state != null) {
          val url = state.electionAdministrationBody.votingLocationFinderUrl
          if (url != null) {
            setIntent(url)
          }
        }
      }
    }

    binding.stateBallot.setOnClickListener {
      viewModel.electionDetails.value.let { state ->
        if (state != null) {
          val url = state.electionAdministrationBody.ballotInfoUrl
          if (url != null) {
            setIntent(url)
          }
        }
      }
    }

    binding.voterFollowOrUnfollowButton.setOnClickListener {
      viewModel.onButtonClick()
    }

    enableMyLocation()
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
        getDeviceLocation()
      }
    } else {
      Snackbar.make(
        this.requireView(),
        "Please grant permission to access location",
        Snackbar.LENGTH_INDEFINITE
      ).setAction("Settings") {
        startActivity(Intent().apply {
          action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
          data = Uri.fromParts("package", activity?.application?.packageName, null)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
      }.show()
    }
  }

  private fun isPermissionGranted(): Boolean {
    return (ActivityCompat.checkSelfPermission(this.requireContext(),
                                               Manifest.permission.ACCESS_FINE_LOCATION)
      == PackageManager.PERMISSION_GRANTED)
  }

  @SuppressLint("MissingPermission")
  private fun enableMyLocation() {
    if (isPermissionGranted()) {
      Log.d(TAG, "Location access is granted")
      getDeviceLocation()
    } else {
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                         REQUEST_LOCATION_PERMISSION)
    }
  }

  @SuppressLint("MissingPermission")
  private fun getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
    try {
      if (isPermissionGranted()) {
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
          if (task.isSuccessful) {
            val lastKnownLocation = task.result
            Log.i(TAG,
                  "Current location is ${lastKnownLocation?.latitude} ${lastKnownLocation?.longitude}")
            if (lastKnownLocation != null) {
              try {
                val address =
                  Geocoder(requireContext()).getFromLocation(lastKnownLocation.latitude,
                                                             lastKnownLocation.longitude,
                                                             1).firstOrNull()
                viewModel.loadDetails(address)
              } catch (e: Exception) {
                Log.e(TAG, "Exception: %s", e)
                Toast.makeText(this.requireContext(),
                               getString(R.string.error_failed_get_address_from_location),
                               Toast.LENGTH_SHORT).show()
              }
            }
          } else {
            Log.d(TAG, "Current location is null. Using defaults.")
            Log.e(TAG, "Exception: %s", task.exception)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Exception: %s", e)
    }
  }

  private fun setIntent(url: String) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    this.startActivity(intent)
  }

//  override fun onCreateView(
//    inflater: LayoutInflater,
//    container: ViewGroup?,
//    savedInstanceState: Bundle?
//  ): View? {
//
//    //TODO: Add ViewModel values and create ViewModel
//
//    //TODO: Add binding values
//
//    //TODO: Populate voter info -- hide views without provided data.
//    /**
//    Hint: You will need to ensure proper data is provided from previous fragment.
//     */
//
//
//    //TODO: Handle loading of URLs
//
//    //TODO: Handle save button UI state
//    //TODO: cont'd Handle save button clicks
//
//    TODO()
//
//  }
//
//  //TODO: Create method to load URL intents

}
package com.example.android.politicalpreparedness.election

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Locale

class ElectionsFragment : Fragment() {

  //TODO: Declare ViewModel
  private val TAG = ElectionsFragment::class.java.simpleName
  private val REQUEST_LOCATION_PERMISSION = 1
  private val viewModel: ElectionsViewModel by lazy {
    val activity = requireNotNull(this.activity) {
      "You can only access the viewModel after onViewCreated()"
    }
    ViewModelProvider(this,
                      ElectionsViewModelFactory(activity.application))[ElectionsViewModel::class.java]
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = FragmentElectionBinding.inflate(inflater)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel

    val adapter = ElectionListAdapter(ElectionListAdapter.ElectionClickListener {
      viewModel.displayElectionDetails(it)
    })

    val savedElectionsAdapter = ElectionListAdapter(ElectionListAdapter.ElectionClickListener {
      viewModel.displayElectionDetails(it)
    })

    binding.upcomingElectionRecycler.adapter = adapter

    binding.savedElectionRecycler.adapter = savedElectionsAdapter

    viewModel.upcomingElections.observe(viewLifecycleOwner) {
      it.let {
        adapter.submitList(it)
      }
    }

    viewModel.savedElections.observe(viewLifecycleOwner) {
      it.let {
        savedElectionsAdapter.submitList(it)
      }
    }

    viewModel.navigateToSelectedElection.observe(this) {
      if (null != it) {
        this.findNavController()
          .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it,
                                                                                           viewModel.isElectionSaved(
                                                                                             it.id)))
        viewModel.displayElectionDetailsComplete()
      }
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
      }
    } else {
      Toast.makeText(this.requireContext(), "Please grant location access", Toast.LENGTH_SHORT)
        .show()
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
    } else {
      requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                         REQUEST_LOCATION_PERMISSION)
    }
  }


  //TODO: Add ViewModel values and create ViewModel

  //TODO: Add binding values

  //TODO: Link elections to voter info

  //TODO: Initiate recycler adapters

  //TODO: Populate recycler adapters

  //TODO: Refresh adapters when fragment loads

}

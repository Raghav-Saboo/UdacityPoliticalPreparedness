package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository

//TODO: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(
  val application: Application,
  val election: Election,
  val isSaved: Boolean
) : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return VoterInfoViewModel(application, election, isSaved) as T
    }
    throw IllegalArgumentException("Unable to construct viewmodel")
  }
}
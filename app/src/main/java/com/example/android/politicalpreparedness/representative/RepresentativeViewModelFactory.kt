package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class RepresentativeViewModelFactory() : ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(RepresentativeViewModel::class.java)) {
      @Suppress("UNCHECKED_CAST")
      return RepresentativeViewModel() as T
    }
    throw IllegalArgumentException("Unable to construct viewmodel")
  }
}
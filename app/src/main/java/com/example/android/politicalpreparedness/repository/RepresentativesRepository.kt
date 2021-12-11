package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApi.retrofitService
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RepresentativesRepository() {

  suspend fun getRepresentatives(address: Address): RepresentativeResponse {
    return withContext(Dispatchers.IO) {
      retrofitService.getRepresentatives(address.toFormattedString())
    }
  }

}
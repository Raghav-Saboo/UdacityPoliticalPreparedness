package com.example.android.politicalpreparedness.repository

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ElectionsFragment
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionsRepository(private val database: ElectionDatabase) {

  private val TAG = ElectionsRepository::class.java.simpleName

  suspend fun getUpcomingElections(): List<Election> {
    return withContext(Dispatchers.IO) {
      try {
        CivicsApi.retrofitService.getElections().elections
      } catch (e: Exception) {
        Log.e(TAG, "Exception: %s", e)
        emptyList<Election>()
      }
    }
  }

  suspend fun getVoterInto(address: String, electionId: Int): State? {
    return withContext(Dispatchers.IO) {
      try {
        CivicsApi.retrofitService.getVoterInfo(address, electionId).state?.first()
      } catch (e: Exception) {
        Log.e(TAG, "Exception: %s", e)
        null
      }
    }
  }

  suspend fun getSavedElections(): List<Election> {
    return database.electionDao.getElectionsSync()
  }

  suspend fun followUpcomingElection(election: Election) {
    database.electionDao.saveElection(election)
  }

  suspend fun unFollowUpcomingElection(id: Int) {
    database.electionDao.deleteElection(id)
  }

}

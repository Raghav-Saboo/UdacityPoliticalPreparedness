package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Intent
import android.location.Address
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

class VoterInfoViewModel(
  application: Application,
  val election: Election,
  val isSaved: Boolean
) : ViewModel() {

  private val database = ElectionDatabase.getInstance(application)

  private val repository = ElectionsRepository(database)

  private val _electionDetails: MutableLiveData<State> = MutableLiveData()
  val electionDetails: LiveData<State>
    get() = _electionDetails

  private val _navigateToElectionsList: MutableLiveData<Boolean?> = MutableLiveData()
  val navigateToElectionsList: LiveData<Boolean?>
    get() = _navigateToElectionsList

  fun loadDetails(address: Address?) {
    viewModelScope.launch {
      val exactAddress = "${address?.getAddressLine(0)}"
      val response = repository.getVoterInto(exactAddress, election.id)
      _electionDetails.value = response
    }
  }

  fun onButtonClick() {
    viewModelScope.launch {
      if (isSaved) repository.unFollowUpcomingElection(election.id)
      else repository.followUpcomingElection(election)
      _navigateToElectionsList.value = true
    }
  }

  fun displayElectionListComplete() {
    _navigateToElectionsList.value = null
  }

  //TODO: Add live data to hold voter info

  //TODO: Add var and methods to populate voter info

  //TODO: Add var and methods to support loading URLs

  //TODO: Add var and methods to save and remove elections to local database
  //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

  /**
   * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
   */

}
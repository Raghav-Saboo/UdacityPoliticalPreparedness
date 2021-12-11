package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(application: Application) : ViewModel() {

  private val _loading = MutableLiveData(false)
  val loading: LiveData<Boolean>
    get() = _loading

  private val database = ElectionDatabase.getInstance(application)

  private val repository = ElectionsRepository(database)

  private var _upcomingElections = MutableLiveData<List<Election>>()
  val upcomingElections: LiveData<List<Election>>
    get() = _upcomingElections

  private var _savedElections = MutableLiveData<List<Election>>()
  val savedElections: LiveData<List<Election>>
    get() = _savedElections

  private val _navigateToUpcomingElection = MutableLiveData<Election?>()

  val navigateToSelectedElection: LiveData<Election?>
    get() = _navigateToUpcomingElection

  init {
    _loading.value = true
    viewModelScope.launch {
      _upcomingElections.value = repository.getUpcomingElections()
      _savedElections.value = repository.getSavedElections()
      _loading.value = false
    }
  }

  fun displayElectionDetails(election: Election) {
    _navigateToUpcomingElection.value = election
  }

  fun displayElectionDetailsComplete() {
    _navigateToUpcomingElection.value = null
  }

  fun isElectionSaved(id: Int): Boolean {
    return _savedElections.value?.map { it.id }?.contains(id) ?: false
  }

  //TODO: Create live data val for upcoming elections

  //TODO: Create live data val for saved elections

  //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

  //TODO: Create functions to navigate to saved or upcoming election voter info

}
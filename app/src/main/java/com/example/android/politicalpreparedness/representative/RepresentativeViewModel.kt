package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativesRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

  //TODO: Establish live data for representatives and address

  //TODO: Create function to fetch representatives from API from a provided address

  /**
   *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

  val (offices, officials) = getRepresentativesDeferred.await()
  _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

  Note: getRepresentatives in the above code represents the method used to fetch data from the API
  Note: _representatives in the above code represents the established mutable live data housing representatives

   */

  //TODO: Create function get address from geo location

  //TODO: Create function to get address from individual fields

  val loading = MutableLiveData<Boolean>(false)

  val representatives = MutableLiveData<List<Representative>>()

  val addressLine1 = MutableLiveData<String>()

  val addressLine2 = MutableLiveData<String>()

  val city = MutableLiveData<String>()

  val zip = MutableLiveData<String>()

  val state = MutableLiveData<String>()

  private val repository = RepresentativesRepository()

  val errMsg = MutableLiveData<String?>()

  fun searchRepresentatives(address: Address) {
    addressLine1.value = address.line1
    addressLine2.value = address.line2
    city.value = address.city
    zip.value = address.zip
    state.value = address.state
    searchRepresentatives()
  }

  fun searchRepresentatives() {
    when {
      addressLine1.value.isNullOrEmpty() -> {
        errMsg.value = "Address Line 1 cannot be null or empty"
      }
      addressLine2.value.isNullOrEmpty() -> {
        errMsg.value = "Address Line 2 cannot be null or empty"
      }
      city.value.isNullOrEmpty() -> {
        errMsg.value = "City cannot be null or empty"
      }
      zip.value.isNullOrEmpty() -> {
        errMsg.value = "Zip cannot be null or empty"
      }
      state.value.isNullOrEmpty() -> {
        errMsg.value = "State cannot be null or empty"
      }
      else -> {
        getRepresentatives(Address(addressLine1.value!!,
                                   addressLine2.value!!,
                                   zip.value!!,
                                   city.value!!,
                                   state.value!!))
      }
    }
  }

  private fun getRepresentatives(address: Address) {
    loading.value = true
    viewModelScope.launch {
      val (offices, officials) = repository.getRepresentatives(address)
      representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
      loading.value = false
    }
  }

  fun setState(state: String) {
    this.state.value = state
  }

  fun postErrorMsgToast() {
    errMsg.value = null
  }

}

package com.example.android.politicalpreparedness.representative.adapter

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.fadeIn
import com.example.android.politicalpreparedness.election.adapter.fadeOut

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
  src?.let {
    val uri = src.toUri().buildUpon().scheme("https").build()
    Glide.with(view.context)
      .load(uri)
      .error(R.drawable.ic_profile)
      .placeholder(R.drawable.ic_profile)
      .into(view)
    //TODO: Add Glide call to load image and circle crop - user ic_profile as a placeholder and for errors.
  }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
  val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
  val position = when (adapter.getItem(0)) {
    is String -> adapter.getPosition(value)
    else -> this.selectedItemPosition
  }
  if (position >= 0) {
    setSelection(position)
  }
}

@BindingAdapter("showProgress")
fun bindProgressView(view: ProgressBar, loadingData: MutableLiveData<Boolean>) {
  loadingData.value?.let {
    if (it) {
      view.fadeIn()
    } else {
      view.fadeOut()
    }
  }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
  return adapter as ArrayAdapter<T>
}

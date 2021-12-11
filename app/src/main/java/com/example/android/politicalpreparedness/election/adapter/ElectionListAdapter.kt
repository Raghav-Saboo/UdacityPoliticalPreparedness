package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ListItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat
import java.util.Locale

class ElectionListAdapter(private val electionClickListener: ElectionClickListener) :
  ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val view = layoutInflater
      .inflate(R.layout.list_item_election, parent, false) as ConstraintLayout
    return ElectionViewHolder.from(view)
  }

  override fun onBindViewHolder(holderElection: ElectionViewHolder, position: Int) {
    val election = getItem(position)
    holderElection.bind(election)
    holderElection.itemView.setOnClickListener {
      electionClickListener.onClick(election)
    }
  }

  class ElectionClickListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
  }

  class ElectionViewHolder private constructor(val binding: ListItemElectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(election: Election) {
      binding.election = election
      binding.dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
      binding.executePendingBindings()
    }

    companion object {
      fun from(parent: ViewGroup): ElectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemElectionBinding.inflate(layoutInflater, parent, false)
        return ElectionViewHolder(binding)
      }
    }
  }

  class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
      return oldItem == newItem
    }
  }

}

//class ElectionListAdapter(private val clickListener: ElectionListener) :
//  ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {
//
//  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
//    return ElectionViewHolder.from(parent)
//  }
//
//  //TODO: Bind ViewHolder
//
//  //TODO: Add companion object to inflate ViewHolder (from)
//}

//TODO: Create ElectionViewHolder

//TODO: Create ElectionDiffCallback

//TODO: Create ElectionListener
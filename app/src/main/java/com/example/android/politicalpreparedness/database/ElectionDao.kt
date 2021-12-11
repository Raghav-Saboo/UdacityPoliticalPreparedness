package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

  //TODO: Add insert query
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveElection(election: Election)

  @Query("select * from election_table order by electionDay")
  suspend fun getElectionsSync(): List<Election>

  //TODO: Add select single election query
  @Query("select * from election_table where id = :id")
  fun getElectionById(id: Int): LiveData<Election>

  //TODO: Add delete query
  @Query("delete from election_table where id = :id")
  suspend fun deleteElection(id: Int)

  //TODO: Add clear query
  @Query("delete from election_table")
  suspend fun deleteAllElections()

}
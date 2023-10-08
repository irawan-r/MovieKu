package com.amora.movieku.data.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amora.movieku.data.model.network.RemoteKeysPopular
import com.amora.movieku.data.model.network.RemoteKeysUpcoming

@Dao
interface RemoteKeysDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllKeysPopular(remoteKey: List<RemoteKeysPopular>)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllKeysUpcoming(remoteKey: List<RemoteKeysUpcoming>)

	@Query("SELECT * FROM remote_keys_popular WHERE id = :id")
	suspend fun getRemoteKeysPopularId(id: Long): RemoteKeysPopular?

	@Query("SELECT * FROM remote_keys_upcoming WHERE id = :id")
	suspend fun getRemoteKeysUpcomingId(id: Long): RemoteKeysUpcoming?

	@Query("DELETE FROM remote_keys_popular")
	suspend fun deleteRemoteKeysPopular()

	@Query("DELETE FROM remote_keys_upcoming")
	suspend fun deleteRemoteKeysUpcoming()
}
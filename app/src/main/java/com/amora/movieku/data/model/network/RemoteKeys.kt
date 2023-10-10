package com.amora.movieku.data.model.network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys_popular")
data class RemoteKeysPopular(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	val prevKey: Int?,
	val nextKey: Int?
)

@Entity(tableName = "remote_keys_upcoming")
data class RemoteKeysUpcoming(
	@PrimaryKey(autoGenerate = true)
	val id: Long,
	val prevKey: Int?,
	val nextKey: Int?
)
package org.d3if0055.assessment2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.d3if0055.assessment2.model.Resep

@Dao
interface ResepDao {
    @Insert
    suspend fun insert(resep: Resep)

    @Update
    suspend fun update(resep: Resep)

    @Query("SELECT * FROM resep ORDER BY judul ASC")
    fun getResep(): Flow<List<Resep>>


    @Query("SELECT * FROM resep WHERE id = :id")
    suspend fun getResepById(id: Long): Resep?

    @Query("DELETE FROM resep WHERE id = :id")
    suspend fun deleteById(id: Long)

}
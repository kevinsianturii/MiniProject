package org.d3if0055.assessment2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resep")
data class Resep(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val judul: String,
    val bahan: String,
    val step: String,
    val kategori: String,
    val imageUri: String? = null
)
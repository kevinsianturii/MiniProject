package org.d3if0055.assessment2.ui.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0055.assessment2.database.ResepDao
import org.d3if0055.assessment2.model.Resep

class DetailViewModel(private val dao: ResepDao) : ViewModel() {

    val kategoriList = listOf(
        "Makanan Pembuka (Appetizers)",
        "Makanan Utama (Main Courses)",
        "Makanan Sampingan (Side Dishes)",
        "Makanan Penutup (Desserts)",
        "Makanan Ringan (Snacks)"
    )

    fun insert (judul: String, bahan:String, kategori:String, step:String, imageUri: Uri?){
        val resep = Resep(
            kategori = kategori,
            judul = judul,
            bahan = bahan,
            step = step,
            imageUri = imageUri?.toString()
            )
        viewModelScope.launch(Dispatchers.IO){
            dao.insert(resep)
        }
    }
    suspend fun getResep(id: Long): Resep?{
        return dao.getResepById(id)
    }
    fun update (id: Long, judul: String, bahan: String, kategori: String, step: String, imageUri: Uri?){
        val resep = Resep(
            kategori = kategori,
            id = id,
            judul = judul,
            bahan = bahan,
            step = step,
            imageUri = imageUri?.toString()
        )
        viewModelScope.launch (Dispatchers.IO){
            dao.update(resep)
        }
    }


    fun delete (id: Long){
        viewModelScope.launch (Dispatchers.IO){
            dao.deleteById(id)
        }
    }

}
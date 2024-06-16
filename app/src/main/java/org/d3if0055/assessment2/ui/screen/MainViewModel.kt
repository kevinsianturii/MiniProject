package org.d3if0055.assessment2.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.d3if0055.assessment2.model.Wisata
import org.d3if0055.assessment2.network.ApiStatus
import org.d3if0055.assessment2.network.WisataApi

class MainViewModel: ViewModel() {
    var data = mutableStateOf(emptyList<Wisata>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    init {
        retriveData()
    }

    fun retriveData() {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = WisataApi.service.getWisata()
                status.value = ApiStatus.SUCCES
            } catch (e:Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
}
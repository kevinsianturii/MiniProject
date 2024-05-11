package org.d3if0055.assessment2.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.d3if0055.assessment2.database.ResepDao
import org.d3if0055.assessment2.model.Resep

class MainViewModel(dao: ResepDao) : ViewModel(){
    val data: StateFlow<List<Resep>> = dao.getResep().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )
}
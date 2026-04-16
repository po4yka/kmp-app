package com.po4yka.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.po4yka.app.data.local.dao.SampleDao
import com.po4yka.app.data.local.entity.SampleEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sampleDao: SampleDao,
) : ViewModel() {

    val items: StateFlow<List<SampleEntity>> = sampleDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSampleItem(title: String, description: String) {
        viewModelScope.launch {
            sampleDao.insert(SampleEntity(title = title, description = description))
            Logger.d { "Inserted item: $title" }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            sampleDao.deleteById(id)
            Logger.d { "Deleted item: $id" }
        }
    }
}

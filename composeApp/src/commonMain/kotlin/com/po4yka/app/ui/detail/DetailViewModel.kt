package com.po4yka.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.po4yka.app.data.local.dao.SampleDao
import com.po4yka.app.data.local.entity.SampleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val itemId: Long,
    private val sampleDao: SampleDao,
) : ViewModel() {

    private val _item = MutableStateFlow<SampleEntity?>(null)
    val item: StateFlow<SampleEntity?> = _item.asStateFlow()

    init {
        loadItem()
    }

    private fun loadItem() {
        viewModelScope.launch {
            _item.value = sampleDao.getById(itemId)
            Logger.d { "Loaded item: $itemId" }
        }
    }

    fun delete() {
        viewModelScope.launch {
            sampleDao.deleteById(itemId)
            Logger.d { "Deleted item: $itemId" }
        }
    }
}

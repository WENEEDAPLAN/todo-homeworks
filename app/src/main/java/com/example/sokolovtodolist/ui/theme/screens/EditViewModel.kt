package com.example.sokolovtodolist.ui.theme.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sokolovtodolist.data.TodoRepository
import com.example.sokolovtodolist.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditViewModel(
    private val repository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: String = savedStateHandle["itemId"] ?: "new"
    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    init {
        viewModelScope.launch {
            repository.items.collect { allItems ->
                val existing = if (itemId == "new") null else allItems.find { it.uid == itemId }
                _item.value = existing
            }
        }
    }

    fun saveItem(item: Item) {
        viewModelScope.launch {
            if (itemId == "new") {
                repository.addItem(item)
            } else {
                repository.updateItem(item)
            }
        }
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            try {
                repository.deleteItem(item.uid)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка удаления"
            }
        }
    }
}
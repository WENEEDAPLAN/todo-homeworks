
package com.example.sokolovtodolist.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sokolovtodolist.data.TodoRepository
import com.example.sokolovtodolist.model.Item
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: TodoRepository
) : ViewModel() {

    val items: StateFlow<List<Item>> = repository.items

    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item.uid)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
        }
    }
}
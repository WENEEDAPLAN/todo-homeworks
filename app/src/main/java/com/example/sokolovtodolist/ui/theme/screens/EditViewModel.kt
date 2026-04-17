package com.example.sokolovtodolist.ui.theme.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sokolovtodolist.data.TodoRepository
import com.example.sokolovtodolist.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    private val repository: TodoRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: String = savedStateHandle["itemId"] ?: "new"
    private val _item = MutableStateFlow<Item?>(null)
    val item: StateFlow<Item?> = _item

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

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
            _isSaving.value = true
            _saveError.value = null
            _saveSuccess.value = false
            try {
                if (itemId == "new") {
                    repository.addItem(item)
                } else {
                    repository.updateItem(item)
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = e.message ?: "Ошибка сохранения"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            try {
                repository.deleteItem(item.uid)

                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = e.message ?: "Ошибка удаления"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
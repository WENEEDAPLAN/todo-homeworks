package com.example.sokolovtodolist.ui.theme.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ColorHelper {
    private val _selectedColor = MutableStateFlow<String?>(null)
    val selectedColor: StateFlow<String?> = _selectedColor.asStateFlow()

    fun setSelectedColor(color: String?) {
        _selectedColor.value = color
    }
}
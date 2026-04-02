package com.example.sokolovtodolist.model

enum class Importance {
    unimportant,
    ordinary,
    important
}

val Importance.label: String
    get() = when (this) {
        Importance.unimportant -> "Низкий"
        Importance.ordinary -> "Обычный"
        Importance.important -> "Высокий"
    }
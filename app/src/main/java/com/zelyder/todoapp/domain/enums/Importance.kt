package com.zelyder.todoapp.domain.enums

enum class Importance(val type: String) {
    NONE("basic"),
    LOW("low"),
    HIGH("important");

    companion object {

        fun parse(type: String): Importance {
            return values().find { it.type == type } ?: NONE
        }

    }
}
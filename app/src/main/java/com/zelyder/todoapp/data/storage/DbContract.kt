package com.zelyder.todoapp.data.storage

object DbContract {
    const val DATABASE_NAME = "ToDoApp.db"

    object Tasks {
        const val TABLE_NAME = "tasks"

        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TEXT = "text"
        const val COLUMN_NAME_DONE = "is_done"
        const val COLUMN_NAME_IMPORTANCE = "importance"
        const val COLUMN_NAME_DEADLINE = "deadline"
        const val COLUMN_NAME_CREATED_AT = "created_at"
        const val COLUMN_NAME_UPDATED_AT = "updated_at"

    }

    object DeletedTasks {
        const val TABLE_NAME = "deleted_tasks"

        const val COLUMN_NAME_ID = "id"
    }
}
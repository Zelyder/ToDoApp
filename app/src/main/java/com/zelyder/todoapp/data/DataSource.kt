package com.zelyder.todoapp.data

import com.zelyder.todoapp.domain.enums.Importance
import com.zelyder.todoapp.domain.models.Task


val initTasks =  listOf(
    Task("0", "Купить что-то"),
    Task("1", "Купить что-то, где-то, зачем-то, но зачем не очень понятно"),
    Task("2", "Купить что-то, где-то, зачем-то, но зачем не очень понятно, но точно чтобы показать как обрезается"),
    Task("3", "Купить что-то", importance = Importance.HIGH),
    Task("4", "Купить что-то", importance = Importance.LOW),
    Task("5", "Купить что-то", isDone = true),
    Task("6", "Купить что-то", date = "6 июля 2021"),
    Task("7", "Купить что-то", date = "6 июля 2021"),
    Task("8", "Сдать первое ДЗ", date = "7 июля 2021"),
    Task("9", "Хорошенько выспаться", date = "8 июля 2021"),
    Task("10", "Купить что-то", date = "9 июня 2021"),
    Task("11", "Купить что-то", isDone = true),
    Task("12", "Купить что-то"),
    Task("13", "Купить что-то"),
    Task("14", "Купить что-то")
)
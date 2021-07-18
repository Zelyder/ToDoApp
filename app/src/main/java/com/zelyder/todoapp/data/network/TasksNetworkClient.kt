package com.zelyder.todoapp.data.network

import com.zelyder.todoapp.data.network.api.YandexApi

interface TasksNetworkClient {
    fun yandexApi(): YandexApi
}
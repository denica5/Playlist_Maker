package com.denica.playlistmaker.settings.data

interface StorageClient<T> {
    fun storeData(data: T)
    fun getData(): T?
}
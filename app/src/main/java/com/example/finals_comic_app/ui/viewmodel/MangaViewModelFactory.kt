package com.example.finals_comic_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finals_comic_app.data.repository.MangaRepository

class MangaViewModelFactory(private val repository: MangaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MangaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MangaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

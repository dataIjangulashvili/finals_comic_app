package com.example.finals_comic_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finals_comic_app.data.local.MangaEntity
import com.example.finals_comic_app.data.model.Manga
import com.example.finals_comic_app.data.repository.MangaRepository
import com.example.finals_comic_app.util.Resource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MangaViewModel(private val repository: MangaRepository) : ViewModel() {

    private val _mangaList = MutableStateFlow<List<Manga>>(emptyList())
    val mangaList: StateFlow<List<Manga>> = _mangaList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedManga = MutableStateFlow<Manga?>(null)
    val selectedManga: StateFlow<Manga?> = _selectedManga

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val followingMangaList: StateFlow<List<MangaEntity>> = repository.getAllFollowing()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter

    val filteredFollowingList: StateFlow<List<MangaEntity>> = kotlinx.coroutines.flow.combine(
        followingMangaList,
        _statusFilter
    ) { list, filter ->
        if (filter == "All") list else list.filter { it.readingStatus == filter }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    init {
        fetchTopManga()
        setupSearch()
    }

    private fun setupSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.length > 2 || it.isEmpty() }
                .onEach { 
                    _isLoading.value = true
                    _errorMessage.value = null
                }
                .collect { query ->
                    if (query.isEmpty()) {
                        fetchTopManga()
                    } else {
                        viewModelScope.launch {
                            val result = repository.searchManga(query)
                            when (result) {
                                is Resource.Success -> {
                                    _mangaList.value = result.data?.data ?: emptyList()
                                    _isLoading.value = false
                                }
                                is Resource.Error -> {
                                    _errorMessage.value = result.message
                                    _isLoading.value = false
                                }
                                is Resource.Loading -> {
                                    _isLoading.value = true
                                }
                            }
                        }
                    }
                }
        }
    }

    fun fetchTopManga() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getTopManga()
            when (result) {
                is Resource.Success -> {
                    _mangaList.value = result.data?.data ?: emptyList()
                    _isLoading.value = false
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(filter: String) {
        _statusFilter.value = filter
    }

    fun searchManga(query: String) {
        onSearchQueryChanged(query)
    }

    fun selectManga(manga: Manga) {
        _selectedManga.value = manga
        checkIfFollowing(manga.malId)
    }

    fun selectMangaById(malId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = repository.getMangaDetails(malId)
            when (result) {
                is Resource.Success -> {
                    _selectedManga.value = result.data?.data
                    checkIfFollowing(malId)
                    _isLoading.value = false
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _isLoading.value = false
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun checkIfFollowing(id: Int) {
        viewModelScope.launch {
            _isFollowing.value = repository.isFollowing(id)
        }
    }

    fun toggleFollowing(manga: Manga) {
        viewModelScope.launch {
            if (_isFollowing.value) {
                repository.deleteFollowing(manga.toEntity())
            } else {
                repository.insertFollowing(manga.toEntity())
            }
            _isFollowing.value = !_isFollowing.value
        }
    }

    fun toggleFollowing(entity: MangaEntity) {
        viewModelScope.launch {
            repository.deleteFollowing(entity)
        }
    }

    fun updateReadingStatus(manga: Manga, status: String) {
        viewModelScope.launch {
            if (status == "Not Following") {
                repository.deleteFollowing(manga.toEntity())
                _isFollowing.value = false
            } else {
                val existing = followingMangaList.value.find { it.malId == manga.malId }
                if (existing != null) {
                    repository.updateFollowing(existing.copy(readingStatus = status))
                } else {
                    repository.insertFollowing(manga.toEntity().copy(readingStatus = status))
                }
                _isFollowing.value = true
            }
        }
    }

    fun updateCurrentChapter(mangaId: Int, chapter: Int) {
        viewModelScope.launch {
            val entity = followingMangaList.value.find { it.malId == mangaId }
            if (entity != null) {
                repository.updateFollowing(entity.copy(currentChapter = chapter))
            }
        }
    }

    fun exportFollowing(context: android.content.Context) {
        val following = followingMangaList.value
        if (following.isEmpty()) return

        val csvBuilder = StringBuilder()
        csvBuilder.append("ID,Title,Score,CurrentChapter,Status\n")
        following.forEach {
            val sanitizedTitle = it.title.replace("\"", "\"\"")
            csvBuilder.append("${it.malId},\"$sanitizedTitle\",${it.score ?: 0.0},${it.currentChapter},${it.readingStatus}\n")
        }
        
        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, csvBuilder.toString())
            type = "text/csv"
        }
        val shareIntent = android.content.Intent.createChooser(sendIntent, "Export Manga List")
        context.startActivity(shareIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun Manga.toEntity() = MangaEntity(
        malId = malId,
        title = title,
        imageUrl = images.jpg.largeImageUrl,
        score = score,
        synopsis = synopsis
    )
}

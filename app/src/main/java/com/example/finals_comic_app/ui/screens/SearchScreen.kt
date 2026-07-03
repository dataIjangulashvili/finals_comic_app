package com.example.finals_comic_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finals_comic_app.ui.MangaAdapter
import com.example.finals_comic_app.ui.viewmodel.MangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: MangaViewModel, onMangaClick: (Int) -> Unit) {
    val mangaList by viewModel.mangaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Initialize adapter once
    val mangaAdapter = remember { MangaAdapter(onMangaClick) }

    // Update adapter data whenever mangaList changes
    LaunchedEffect(mangaList) {
        mangaAdapter.submitList(mangaList)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { 
                            viewModel.onSearchQueryChanged(it)
                        },
                        placeholder = { Text("Search manga...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && mangaList.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        RecyclerView(context).apply {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter = mangaAdapter
                        }
                    },
                    update = {
                        // The list update is handled by the LaunchedEffect above
                    }
                )
            }
        }
    }
}

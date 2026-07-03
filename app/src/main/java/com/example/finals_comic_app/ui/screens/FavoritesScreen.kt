package com.example.finals_comic_app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import coil.compose.AsyncImage
import com.example.finals_comic_app.data.local.MangaEntity
import com.example.finals_comic_app.ui.theme.TextSecondary
import com.example.finals_comic_app.ui.viewmodel.MangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: MangaViewModel,
    onMangaClick: (Int) -> Unit
) {
    val following by viewModel.filteredFollowingList.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My List", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            listOf("All", "Reading", "Completed", "Plan to Read").forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        viewModel.onStatusFilterChanged(status)
                                        showFilterMenu = false
                                    },
                                    trailingIcon = {
                                        if (statusFilter == status) {
                                            Icon(Icons.Default.Check, contentDescription = null)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (following.isNotEmpty()) {
                        IconButton(onClick = {
                            val followingList = viewModel.followingMangaList.value
                            val txt = StringBuilder()
                            followingList.forEach { 
                                txt.append("Manga: ${it.title}\n")
                                txt.append("Status: ${it.readingStatus}\n")
                                txt.append("Chapter: ${it.currentChapter}\n\n")
                            }
                            
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, txt.toString())
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Export Manga List")
                            context.startActivity(shareIntent)
                        }) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (following.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (statusFilter == "All") "Your list is empty" else "No manga marked as $statusFilter",
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(following) { manga ->
                    FollowingItem(
                        manga = manga,
                        onDelete = { viewModel.toggleFollowing(manga) },
                        onClick = { onMangaClick(manga.malId) }
                    )
                }
            }
        }
    }
}

@Composable
fun FollowingItem(
    manga: MangaEntity,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = manga.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Chapter ${manga.currentChapter}", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = manga.readingStatus,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

package com.example.finals_comic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.finals_comic_app.data.model.Manga
import com.example.finals_comic_app.ui.theme.TextSecondary
import com.example.finals_comic_app.ui.viewmodel.MangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MangaViewModel, onMangaClick: (Int) -> Unit) {
    val mangaList by viewModel.mangaList.collectAsState()
    val followingList by viewModel.followingMangaList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Panel Catcher", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("The Tracker", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                },
                actions = {
                    // Search and Profile icons removed
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                SectionHeader(title = "New Chapters from Followed Comics")
                if (followingList.isEmpty()) {
                    Text(
                        "No followed comics yet",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = TextSecondary
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(followingList) { manga ->
                            FollowedMangaItem(manga = manga, onClick = { onMangaClick(manga.malId) })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "Reading History")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mangaList.drop(5).take(5)) { manga ->
                        HistoryItem(manga = manga, onClick = { onMangaClick(manga.malId) })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionHeader(title = "New Releases")
                // Using a grid-like layout within the column for New Releases
                mangaList.drop(10).chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { manga ->
                            MangaGridItem(
                                manga = manga,
                                onClick = { onMangaClick(manga.malId) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FollowedMangaItem(manga: com.example.finals_comic_app.data.local.MangaEntity, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = manga.imageUrl,
            contentDescription = manga.title,
            modifier = Modifier
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = manga.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Ch. ${manga.currentChapter}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun MangaGridItem(manga: Manga, onClick: () -> Unit, modifier: Modifier = Modifier, width: Dp = 0.dp) {
    val itemModifier = if (width > 0.dp) modifier.width(width) else modifier
    
    Column(
        modifier = itemModifier.clickable { onClick() }
    ) {
        AsyncImage(
            model = manga.images.jpg.largeImageUrl,
            contentDescription = manga.title,
            modifier = Modifier
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = manga.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Score: ${manga.score ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
fun HistoryItem(manga: Manga, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = manga.images.jpg.largeImageUrl,
            contentDescription = manga.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )
        Text(
            text = manga.title,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

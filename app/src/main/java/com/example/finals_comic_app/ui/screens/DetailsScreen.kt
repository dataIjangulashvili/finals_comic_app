package com.example.finals_comic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.finals_comic_app.ui.theme.TextSecondary
import com.example.finals_comic_app.ui.viewmodel.MangaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(viewModel: MangaViewModel, onBack: () -> Unit) {
    val manga by viewModel.selectedManga.collectAsState()
    val isFollowing by viewModel.isFollowing.collectAsState()
    val followingList by viewModel.followingMangaList.collectAsState()
    
    val currentFollowing = manga?.let { m -> followingList.find { it.malId == m.malId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Removed the plus button as requested
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        manga?.let { m ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier.height(300.dp)) {
                    // Blurred thumbnail photo
                    AsyncImage(
                        model = m.images.jpg.largeImageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alpha = 0.6f
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                    startY = 100f
                                )
                            )
                    )
                }
                
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .offset(y = (-100).dp)
                ) {
                    Text(
                        text = m.title, 
                        style = MaterialTheme.typography.headlineMedium, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusDropdown(
                            currentStatus = currentFollowing?.readingStatus ?: "Not Following",
                            onStatusSelected = { status ->
                                manga?.let { viewModel.updateReadingStatus(it, status) }
                            }
                        )
                        
                        if (isFollowing) {
                            Spacer(modifier = Modifier.width(8.dp))
                            ChapterSelector(
                                currentChapter = currentFollowing?.currentChapter ?: 0,
                                totalChapters = m.chapters ?: 100,
                                onChapterSelected = { viewModel.updateCurrentChapter(m.malId, it) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = m.synopsis ?: "No synopsis available.", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoRow(label = "Genres", value = m.genres.joinToString { it.name })
                    InfoRow(label = "Authors", value = m.authors.joinToString { it.name })
                    InfoRow(label = "Status", value = m.status)

                    m.relations?.filter { it.relation == "Spin-off" }?.takeIf { it.isNotEmpty() }?.let { relations ->
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Spin-offs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        relations.forEach { relation ->
                            relation.entry.filter { it.type == "manga" }.forEach { entry ->
                                Text(
                                    text = "• ${entry.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.selectMangaById(entry.malId) }
                                )
                            }
                        }
                    }

                    m.relations?.filter { it.relation != "Spin-off" }?.takeIf { it.isNotEmpty() }?.let { relations ->
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Other Relations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        relations.forEach { relation ->
                            val entries = relation.entry.filter { it.type == "manga" }
                            if (entries.isNotEmpty()) {
                                Text(
                                    text = "${relation.relation}:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                entries.forEach { entry ->
                                    Text(
                                        text = "• ${entry.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                            .clickable { viewModel.selectMangaById(entry.malId) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusDropdown(currentStatus: String, onStatusSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("Not Following", "Reading", "Completed", "Plan to Read")
    
    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            color = if (currentStatus == "Not Following") Color.Gray else Color(0xFF3B82F6),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentStatus.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp).padding(start = 4.dp)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            statuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    },
                    trailingIcon = {
                        if (currentStatus == status) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ChapterSelector(currentChapter: Int, totalChapters: Int, onChapterSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            color = Color.DarkGray,
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ch. $currentChapter",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            val maxChapters = if (totalChapters > 0) totalChapters else 1000
            val start = maxOf(1, currentChapter - 25)
            val end = minOf(maxChapters, start + 50)
            
            (start..end).forEach { chapter ->
                DropdownMenuItem(
                    text = { Text("Chapter $chapter") },
                    onClick = {
                        onChapterSelected(chapter)
                        expanded = false
                    },
                    trailingIcon = {
                        if (currentChapter == chapter) {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

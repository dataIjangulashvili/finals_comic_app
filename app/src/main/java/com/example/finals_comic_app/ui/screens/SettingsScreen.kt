package com.example.finals_comic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finals_comic_app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var username by remember { mutableStateOf("poseidon") }
    var showEditMenu by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var tempUsername by remember { mutableStateOf(username) }

    Scaffold(
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header: Cover and Profile Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Cover Image Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color(0xFF2C2C2C))
                )
                
                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCCCCCC))
                        .border(4.dp, Color(0xFF121212), CircleShape)
                ) {
                    // Online status dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 4.dp, bottom = 4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                            .border(2.dp, Color(0xFF121212), CircleShape)
                    )
                }

                // Edit Profile Button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                    ) {
                        Text("Edit Profile")
                    }

                    DropdownMenu(
                        expanded = showEditMenu,
                        onDismissRequest = { showEditMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Change Username") },
                            onClick = {
                                showEditMenu = false
                                showNameDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Change Profile Image") },
                            onClick = { showEditMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Change Cover Image") },
                            onClick = { showEditMenu = false }
                        )
                    }
                }
                
                // Camera icon on cover
                IconButton(
                    onClick = { /* Change cover */ },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp, bottom = 50.dp)
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // User Info
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "988 comment karma",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(TextSecondary))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "0 followers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Edit Username") },
            text = {
                OutlinedTextField(
                    value = tempUsername,
                    onValueChange = { tempUsername = it },
                    label = { Text("Username") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    username = tempUsername
                    showNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

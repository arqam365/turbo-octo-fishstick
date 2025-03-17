package com.nextlevelprogrammers.elearns.ui.design

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nextlevelprogrammers.elearns.model.ContentItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(navController: NavController, contentList: List<ContentItem>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Videos", "PDFs")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Section Contents") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // Tab Row
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter content based on selected tab
            val filteredContent = when (selectedTabIndex) {
                0 -> contentList.filter { it.content_type.lowercase() == "video" }
                1 -> contentList.filter { it.content_type.lowercase() == "pdf" }
                else -> emptyList()
            }

            if (filteredContent.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No content available")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredContent) { content ->
                        ContentItemView(navController, content, contentList)
                    }
                }
            }
        }
    }
}

@Composable
fun ContentItemView(navController: NavController, content: ContentItem, contentList: List<ContentItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (content.content_type.lowercase() == "video") {
                    val videoList = contentList.filter { it.content_type.lowercase() == "video" }
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("fullHdUrl", content.full_hd_video_uri ?: "")
                        set("hdUrl", content.hd_video_uri ?: "")
                        set("sdUrl", content.sd_video_uri ?: "")
                        set("nextVideos", videoList)
                    }
                    navController.navigate("videoPlayerScreen")
                } else if (content.content_type.lowercase() == "pdf") {
                    val encodedPdfUrl = URLEncoder.encode(content.pdf_uri ?: "", StandardCharsets.UTF_8.toString())
                    navController.navigate("pdfViewerScreen/$encodedPdfUrl")
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = content.content_name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = content.content_description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Type: ${content.content_type}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
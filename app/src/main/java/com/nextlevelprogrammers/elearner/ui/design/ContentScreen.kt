package com.nextlevelprogrammers.elearner.ui.design

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
import com.nextlevelprogrammers.elearner.Routes
import com.nextlevelprogrammers.elearner.model.ContentItem
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(navController: NavController, contentList: List<ContentItem>) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Videos", "PDFs", "Live")

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

            val filteredContent = when (selectedTabIndex) {
                0 -> contentList.filter { it.content_type.lowercase() == "video" }
                1 -> contentList.filter { it.content_type.lowercase() == "pdf" }
                2 -> contentList.filter { it.content_type.lowercase() == "live_video" }
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
                when (content.content_type.lowercase()) {
                    "video" -> {
                        val videoList = contentList.filter { it.content_type.lowercase() == "video" }
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("fullHdUrl", content.full_hd_video_uri ?: "")
                            set("hdUrl", content.hd_video_uri ?: "")
                            set("sdUrl", content.sd_video_uri ?: "")
                            set("nextVideos", videoList)
                        }
                        navController.navigate("videoPlayerScreen")
                    }
                    "pdf" -> {
                        val encodedPdfUrl = URLEncoder.encode(content.pdf_uri ?: "", StandardCharsets.UTF_8.toString())
                        navController.navigate("pdfViewerScreen/$encodedPdfUrl")
                    }
                    "live_video" -> {
                        navController.navigate("${Routes.YOUTUBE_PLAYER}?videoId=${content.live_video_id}")
                    }
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
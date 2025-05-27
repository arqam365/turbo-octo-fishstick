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
    val tabs = listOf("Videos", "PDFs","Live") //Here I added the extra Section

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
                2-> listOf(ContentItem(content_id = "Wfxd12hNy_E",
                    section_id = "",
                    content_name = "Live Video",
                    content_description = "Playing Live Video",
                    content_index = 1,
                    content_type = "live",
                    pdf_uri = "",
                    pdf_gs_bucket_uri = "",
                    full_hd_video_uri = null,
                    full_hd_video_gs_bucket_uri = null,
                    hd_video_uri = null,
                    hd_video_gs_bucket_uri = null,
                    sd_video_uri = null,
                    sd_video_gs_bucket_uri = null,
                    is_published = true,
                    createdAt = "",
                    updatedAt = ""))
                else -> emptyList()
            }
            //The second index is for live and i had made a mock object but when working will real object make sure to update where i used videoId parameter

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
                else if (content.content_type.lowercase() == "live"){
                    //here i am using content_id to store the live stream code in ContentItem data class object but you will replace it with the parameter where you will store your video id
                    navController.navigate("${Routes.YOUTUBE_PLAYER}?videoId=${content.content_id}")
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
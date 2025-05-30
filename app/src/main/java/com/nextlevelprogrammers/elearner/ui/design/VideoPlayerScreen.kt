package com.nextlevelprogrammers.elearner.ui.design

import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    videoQualities: Map<String, String>, // Keys: "Full HD", "HD", "SD"
    nextVideos: List<String>, // List of next video titles
    onVideoSelected: (String) -> Unit // Callback when a video is selected
) {
    val context = LocalContext.current
    var currentVideoTitle by remember { mutableStateOf(nextVideos.firstOrNull() ?: "") }
    var currentQuality by remember { mutableStateOf("Full HD") }
    var isFullScreen by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoQualities[currentQuality] ?: "")
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer, currentQuality) {
        val url = videoQualities[currentQuality] ?: ""
        Log.d("VideoPlayer", "Current Quality: $currentQuality, URL: $url")

        if (url.isNotBlank()) {
            val mediaItem = MediaItem.fromUri(url)
            val currentPosition = exoPlayer.currentPosition
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.seekTo(currentPosition)
            exoPlayer.playWhenReady = true
        } else {
            Log.e("VideoPlayer", "⚠️ No valid URL for $currentQuality")
        }

        onDispose { }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == androidx.media3.common.Player.STATE_ENDED) {
                    // Autoplay next video
                    val currentIndex = nextVideos.indexOfFirst { it == currentVideoTitle } // Keep track of current video title
                    if (currentIndex != -1 && currentIndex + 1 < nextVideos.size) {
                        onVideoSelected(nextVideos[currentIndex + 1])
                    }
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // Release when exiting screen
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Handle fullscreen
    DisposableEffect(isFullScreen) {
        activity?.let {
            if (isFullScreen) {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                WindowCompat.setDecorFitsSystemWindows(it.window, false)
                it.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                it.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            WindowCompat.setDecorFitsSystemWindows(activity?.window!!, true)
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    Scaffold() { paddingValues ->

        Column(
            modifier = Modifier
                .then(if (!isFullScreen) Modifier.padding(paddingValues) else Modifier)
                .fillMaxSize()
        ) {

            // ExoPlayer View
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f)) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = true
                            setShowNextButton(false)
                            setShowPreviousButton(false)
                        }
                    },
                    modifier = Modifier.matchParentSize()
                )

                // Fullscreen toggle
                Text(
                    text = "⛶ Fullscreen",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clickable { isFullScreen = !isFullScreen }
                )
            }

            if (!isFullScreen) {
                Spacer(modifier = Modifier.height(16.dp))

                // Quality Dropdown
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {

                    OutlinedButton(onClick = { dropdownExpanded = true }) {
                        Text("Quality: $currentQuality")
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        videoQualities.keys.forEach { quality ->
                            DropdownMenuItem(
                                text = { Text(quality) },
                                onClick = {
                                    currentQuality = quality
                                    val url = videoQualities[quality] ?: ""
                                    if (url.isNotBlank()) {
                                        val mediaItem = MediaItem.fromUri(url)
                                        val currentPosition = exoPlayer.currentPosition
                                        exoPlayer.setMediaItem(mediaItem)
                                        exoPlayer.prepare()
                                        exoPlayer.seekTo(currentPosition)
                                        exoPlayer.playWhenReady = true
                                    }
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Up Next:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                LazyColumn {
                    items(nextVideos) { videoTitle ->
                        Text(
                            text = videoTitle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onVideoSelected(videoTitle) }
                                .padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
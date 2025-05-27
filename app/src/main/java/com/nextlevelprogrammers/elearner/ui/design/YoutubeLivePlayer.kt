package com.nextlevelprogrammers.elearner.ui.design

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun YouTubeLivePlayer(videoId: String, modifier: Modifier = Modifier,navController: NavController) {
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val activity = context as? Activity
    BackHandler {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        navController.popBackStack()
    }

    LaunchedEffect(isLoading) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    Box(modifier = modifier) {
        AndroidView(factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.pluginState = WebSettings.PluginState.ON
                webChromeClient = WebChromeClient() // Enables full screen
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        isLoading = false
                    }
                }

                setOnTouchListener { _, _ -> true }
                isLongClickable = false
                setOnLongClickListener { true }


                loadData(
                    """
                    <html>
                      <body style="margin:0;padding:0;overflow:hidden;">
                        <iframe width="100%" height="100%"
                            src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&fs=1&modestbranding=1&controls=1&rel=0&showinfo=0&iv_load_policy=3"
                            frameborder="0"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; fullscreen"
                            allowfullscreen>
                        </iframe>
                      </body>
                    </html>
                    """.trimIndent(),
                    "text/html",
                    "utf-8"
                )
            }
        }, update = {
            it.settings.javaScriptEnabled = true
        }, modifier = Modifier.fillMaxSize())

        if (isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        }
    }
}

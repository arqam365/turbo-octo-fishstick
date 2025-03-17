package com.nextlevelprogrammers.elearns.ui.design

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(pdfUrl: String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PDF Viewer") }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$pdfUrl")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
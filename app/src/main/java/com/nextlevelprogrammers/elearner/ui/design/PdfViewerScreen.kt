package com.nextlevelprogrammers.elearner.ui.design

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(pdfUrl: String) {
    val context = LocalContext.current
    val pdfBitmapConverter = remember { PdfBitmapConverter(context) }
    var renderedPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var pdfUri by remember { mutableStateOf<Uri?>(null) }

    var page by remember { mutableIntStateOf(0) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    fun resetScale() {
        scale = 1f
        offset = Offset.Zero
    }

    LaunchedEffect(pdfUri) {
        pdfUri?.let {
            renderedPages = pdfBitmapConverter.pdfToBitmaps(it)
        }
    }

    LaunchedEffect(Unit) {
        val pdfFile = File(context.cacheDir, "downloaded.pdf")
        try {
            pdfUri = downloadPdfFromUrl(pdfUrl, pdfFile, context)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PDF Viewer") },
                modifier = Modifier.shadow(1.dp)
            )
        },
        bottomBar = {
            if (renderedPages.isNotEmpty()) {
                BottomBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(), // <- crucial for bottom safe area
                    onNext = { if (page < renderedPages.lastIndex) page++ else page = 0 },
                    onPrev = { if (page > 0) page-- else page = renderedPages.lastIndex },
                    page = page,
                    total_pages = renderedPages.size,
                    resetScale = { resetScale() }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                pdfUri == null -> CircularProgressIndicator()
                renderedPages.isEmpty() -> CircularProgressIndicator()
                else -> {
                    val currentPage = renderedPages[page]
                    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
                        val width = currentPage.width.toFloat()
                        val height = currentPage.height.toFloat()
                        scale = (scale * zoomChange).coerceIn(1f, 5f)
                        val extraWidth = (scale - 1) * width
                        val extraHeight = (scale - 1) * height
                        val maxX = extraWidth / 2
                        val maxY = extraHeight / 2
                        offset = Offset(
                            x = (offset.x + scale * offsetChange.x).coerceIn(-maxX, maxX),
                            y = (offset.y + scale * offsetChange.y).coerceIn(-maxY, maxY)
                        )
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = currentPage.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(currentPage.width.toFloat() / currentPage.height.toFloat())
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        translationX = offset.x
                                        translationY = offset.y
                                    }
                                    .transformable(state = transformState)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    modifier: Modifier,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    page: Int,
    total_pages: Int,
    resetScale: () -> Unit
)
{

    Row(modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
        .shadow(1.dp)
        .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically)
    {
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier=Modifier.size(28.dp).clickable{onPrev()
                resetScale()})


        Text("${page+1}/$total_pages", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontSize = 16.sp)

        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier=Modifier.size(28.dp).clickable { onNext()
                resetScale()})
    }
}

suspend fun downloadPdfFromUrl(pdfUrl: String, outputFile: File,context: Context): Uri? {
    return withContext(Dispatchers.IO) {
        val url = URL(pdfUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.inputStream.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
            Uri.fromFile(outputFile)
        }
    }
}
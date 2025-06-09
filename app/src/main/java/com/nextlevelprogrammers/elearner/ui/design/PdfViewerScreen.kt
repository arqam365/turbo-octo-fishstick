package com.nextlevelprogrammers.elearner.ui.design

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
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
    val pdfBitmapConverter = remember {
        PdfBitmapConverter(context)
    }
    var renderedPages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }
    var pdfUri by remember {
        mutableStateOf<Uri?>(null)
    }


    LaunchedEffect(pdfUri) {
        Log.d("PDFURI", pdfUri.toString())
        pdfUri?.let { uri ->
            renderedPages=pdfBitmapConverter.pdfToBitmaps(pdfUri!!)
        }
    }

    LaunchedEffect(Unit) {
        val pdfFile = File(context.cacheDir, "downloaded.pdf")
        try {
           pdfUri= downloadPdfFromUrl(pdfUrl, pdfFile,context)
        }
        catch (e: IOException)
        {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PDF Viewer") },
                modifier = Modifier.shadow(1.dp)
            )
        }
    ) { paddingValues ->

        val pad=paddingValues

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {
            if (pdfUri==null){
                CircularProgressIndicator()
            }
            else{
                if (renderedPages.isEmpty()){
                    CircularProgressIndicator()
                }
                else {
                    PdfScreen(renderedPages, modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun PdfScreen(renderedPages: List<Bitmap>, modifier: Modifier){
    var page by remember { mutableIntStateOf(0) }
    Column(modifier=modifier.fillMaxSize()){
        var scale by remember { mutableFloatStateOf(1f) }
        var offSet by remember { mutableStateOf(Offset.Zero) }
        BoxWithConstraints(modifier = modifier.weight(1f), contentAlignment = Alignment.Center)
        {
            val width = with(LocalDensity.current) { maxWidth.toPx() }
            val height = with(LocalDensity.current) { maxHeight.toPx() }

            val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)

                val extraWidth=(scale-1)*width
                val extraHeight=(scale-1)*height

                val maxX=extraWidth/2
                val maxy=extraHeight/2

                offSet=Offset(
                    x=(offSet.x+ scale*offsetChange.x).coerceIn(-maxX,maxX),
                    y=(offSet.y+ scale*offsetChange.y).coerceIn(-maxy,maxy)
                )
            }

            Image(
                bitmap = renderedPages[page].asImageBitmap(),
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .aspectRatio(renderedPages[page].width.toFloat() / renderedPages[page].height.toFloat())
                    .drawWithContent {
                        drawContent()
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offSet.x
                        translationY = offSet.y
                    }
                    .transformable(state = state)
            )

        }
        Box(modifier = modifier.fillMaxWidth()) {
            BottomBar(
                modifier = modifier,
                onNext = { if (page < renderedPages.size - 1) page++ else page = 0 },
                onPrev = { if (page > 0) page-- else page = renderedPages.size - 1 },
                page = page,
                resetScale={
                    scale=1f
                    offSet=Offset.Zero
                },
                total_pages = renderedPages.size
            )
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

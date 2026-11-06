package com.sportify.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.sportify.app.download.Downloads
import kotlinx.coroutines.delay
import com.sportify.app.data.Track
import com.sportify.app.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SportifyTheme { SportifyApp() } }
    }
}

@Composable
fun SportifyApp() {
    var tab by remember { mutableIntStateOf(0) }
    var current by remember { mutableStateOf<Track?>(null) }
    var playing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    BackHandler(expanded) { expanded = false }
    val ctx = LocalContext.current
    var downloadCount by remember { mutableIntStateOf(Downloads.list(ctx).size) }
    var downloaded by remember { mutableStateOf(false) }
    LaunchedEffect(current) {
        while (true) {
            downloaded = current?.let { Downloads.isDownloaded(ctx, it) } ?: false
            downloadCount = Downloads.list(ctx).size
            delay(2000)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = SColors.Bg,
            bottomBar = {
                Column {
                    current?.let { MiniPlayer(it, playing, { playing = !playing }, { expanded = true }) }
                    NavigationBar(containerColor = SColors.Bg) {
                        listOf("Home" to Icons.Rounded.Home, "Search" to Icons.Rounded.Search,
                            "Library" to Icons.Rounded.LibraryMusic).forEachIndexed { i, (label, icon) ->
                            NavigationBarItem(tab == i, { tab = i }, { Icon(icon, label) }, label = { Text(label) })
                        }
                    }
                }
            }
        ) { pad ->
            Box(Modifier.padding(pad)) {
                when (tab) {
                    0 -> HomeScreen { current = it; playing = true }
                    1 -> SearchScreen()
                    else -> LibraryScreen(downloadCount)
                }
            }
        }
        AnimatedVisibility(expanded && current != null,
            enter = slideInVertically { it }, exit = slideOutVertically { it }) {
            current?.let { t ->
                NowPlayingScreen(t, playing, downloaded, { playing = !playing }, {
                    when {
                        downloaded -> Downloads.delete(ctx, t)
                        t.audioUrl == null -> Toast.makeText(ctx, "This track has no downloadable source", Toast.LENGTH_SHORT).show()
                        else -> { Downloads.enqueue(ctx, t); Toast.makeText(ctx, "Downloading", Toast.LENGTH_SHORT).show() }
                    }
                }, { expanded = false })
            }
        }
    }
}

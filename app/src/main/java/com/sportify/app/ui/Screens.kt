package com.sportify.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportify.app.data.Track
import com.sportify.app.data.sampleTracks

@Composable
fun Art(track: Track, size: Modifier) {
    Box(
        size.clip(RoundedCornerShape(6.dp))
            .background(Brush.linearGradient(listOf(track.color, track.color.copy(alpha = .55f))))
    )
}

@Composable
fun HomeScreen(onPlay: (Track) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Text("Good evening", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 56.dp, bottom = 16.dp))
        }
        items(sampleTracks.chunked(2)) { row ->
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { t ->
                    Row(
                        Modifier.weight(1f).height(56.dp).clip(RoundedCornerShape(6.dp))
                            .background(SColors.Surface).clickable { onPlay(t) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Art(t, Modifier.size(56.dp))
                        Text(t.title, Modifier.padding(horizontal = 10.dp), fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, maxLines = 2)
                    }
                }
            }
        }
        item {
            Text("Made for you", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(sampleTracks.reversed()) { t ->
                    Column(Modifier.width(140.dp).clickable { onPlay(t) }) {
                        Art(t, Modifier.size(140.dp))
                        Text(t.title, Modifier.padding(top = 8.dp), fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text(t.artist, color = SColors.Muted, fontSize = 12.sp, maxLines = 1)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SearchScreen() {
    var q by remember { mutableStateOf("") }
    val genres = listOf("Pop", "Hip-Hop", "Indie", "Electronic", "Chill", "Workout", "Focus", "Jazz")
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Search", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 40.dp, bottom = 12.dp))
        OutlinedTextField(
            q, { q = it }, Modifier.fillMaxWidth(), singleLine = true,
            placeholder = { Text("Songs, artists, playlists") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(Modifier.height(16.dp))
        genres.chunked(2).forEachIndexed { r, row ->
            Row(Modifier.fillMaxWidth().padding(bottom = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEachIndexed { i, g ->
                    Box(
                        Modifier.weight(1f).height(84.dp).clip(RoundedCornerShape(8.dp))
                            .background(sampleTracks[(r * 2 + i) % sampleTracks.size].color).padding(12.dp)
                    ) { Text(g, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                }
            }
        }
    }
}

@Composable
fun LibraryScreen(downloadCount: Int) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Library", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 40.dp, bottom = 16.dp))
        listOf("Downloads", "Liked Songs", "Road Trip", "Late Night Focus", "Throwbacks").forEachIndexed { i, name ->
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Art(sampleTracks[i % sampleTracks.size], Modifier.size(56.dp))
                Column(Modifier.padding(start = 12.dp)) {
                    Text(name, fontWeight = FontWeight.SemiBold)
                    Text(if (name == "Downloads") "$downloadCount songs on this phone" else "Playlist",
                        color = SColors.Muted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(track: Track, playing: Boolean, onToggle: () -> Unit, onOpen: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp)).background(track.color.copy(alpha = .85f))
            .clickable(onClick = onOpen).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Art(track, Modifier.size(40.dp))
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(track.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
            Text(track.artist, fontSize = 12.sp, color = SColors.Text.copy(alpha = .75f), maxLines = 1)
        }
        IconButton(onToggle) {
            Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, "Play or pause")
        }
    }
}

@Composable
fun NowPlayingScreen(
    track: Track, playing: Boolean, downloaded: Boolean,
    onToggle: () -> Unit, onDownload: () -> Unit, onClose: () -> Unit
) {
    // Stand-in for the looping short video behind the player. Swap this Box for an ExoPlayer view later.
    val t = rememberInfiniteTransition(label = "backdrop")
    val shift by t.animateFloat(0f, 1f, infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse), label = "shift")
    var progress by remember { mutableFloatStateOf(0.3f) }

    Box(
        Modifier.fillMaxSize().background(
            Brush.linearGradient(
                listOf(track.color, SColors.Pink.copy(alpha = .5f), SColors.Bg),
                start = Offset(0f, 300f * shift), end = Offset(900f * (0.6f + shift), 2200f)
            )
        )
    ) {
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().padding(top = 32.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClose) { Icon(Icons.Rounded.KeyboardArrowDown, "Close player", Modifier.size(32.dp)) }
                Text("Playing from Made for you", Modifier.weight(1f), fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text(track.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(track.artist, color = SColors.Text.copy(alpha = .75f))
                }
                IconButton(onDownload) {
                    Icon(if (downloaded) Icons.Rounded.DownloadDone else Icons.Rounded.Download,
                        if (downloaded) "Downloaded" else "Download")
                }
                Icon(Icons.Rounded.FavoriteBorder, "Like")
            }
            Slider(progress, { progress = it }, Modifier.padding(top = 12.dp),
                colors = SliderDefaults.colors(thumbColor = SColors.Text, activeTrackColor = SColors.Text))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("1:04", fontSize = 12.sp); Text("3:32", fontSize = 12.sp)
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Shuffle, "Shuffle")
                Icon(Icons.Rounded.SkipPrevious, "Previous", Modifier.size(36.dp))
                Box(Modifier.size(68.dp).clip(CircleShape).background(SColors.Text).clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center) {
                    Icon(if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, "Play or pause",
                        Modifier.size(38.dp), tint = SColors.Bg)
                }
                Icon(Icons.Rounded.SkipNext, "Next", Modifier.size(36.dp))
                Icon(Icons.Rounded.Repeat, "Repeat")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

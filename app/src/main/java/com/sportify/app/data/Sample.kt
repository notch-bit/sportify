package com.sportify.app.data

import androidx.compose.ui.graphics.Color

data class Track(val id: String, val title: String, val artist: String, val color: Color,
    val audioUrl: String? = null // direct audio URL from a source that permits downloads
)

// Placeholder data. Replaced later by Spotify playlists (login step).
val sampleTracks = listOf(
    Track("1", "Midnight Drive", "Neon Arcade", Color(0xFF5B3FD6)),
    Track("2", "Salt & Static", "Harbor Lights", Color(0xFFD6457A)),
    Track("3", "Slow Orbit", "Kiran Vale", Color(0xFF1F8A8A)),
    Track("4", "Paper Planes", "The Overtones", Color(0xFFE08A2E)),
    Track("5", "Low Tide", "Mara Quinn", Color(0xFF2E5BE0)),
    Track("6", "Glasshouse", "Odd Hours", Color(0xFF3FAE6B)),
)

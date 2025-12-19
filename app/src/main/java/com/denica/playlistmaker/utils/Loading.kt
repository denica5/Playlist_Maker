package com.denica.playlistmaker.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.denica.playlistmaker.R

@Composable
fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        CircularProgressIndicator(
            color = colorResource(R.color.blue),
            modifier = Modifier.size(44.dp)
        )
    }
}
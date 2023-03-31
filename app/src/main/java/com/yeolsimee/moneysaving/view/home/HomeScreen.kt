package com.yeolsimee.moneysaving.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yeolsimee.moneysaving.view.calendar.CalendarScreen
import com.yeolsimee.moneysaving.view.calendar.CalendarViewModel

@Composable
fun HomeScreen(viewModel: CalendarViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CalendarScreen(viewModel)
    }
}
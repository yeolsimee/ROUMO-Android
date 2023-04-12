@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.yeolsimee.moneysaving.view.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.yeolsimee.moneysaving.R
import com.yeolsimee.moneysaving.domain.calendar.CalendarDay
import com.yeolsimee.moneysaving.ui.PrText
import com.yeolsimee.moneysaving.ui.calendar.DayOfMonthIcon

@ExperimentalFoundationApi
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {

    Column(Modifier.fillMaxSize()) {
        val dialogState = remember { mutableStateOf(false) }
        YearMonthDialog(dialogState, viewModel)

        AppLogoImage()
        Spacer(Modifier.height(16.dp))
        YearMonthSelectBox(dialogState, viewModel)
        ComposeCalendar(viewModel)
    }
}

@Composable
private fun AppLogoImage() {
    Box(
        modifier = Modifier
            .background(Color.Red)
            .width(98.dp)
            .height(30.dp)
    ) {
        PrText(text = "앱 로고 영역", color = Color.White)
    }
}

@Composable
private fun YearMonthSelectBox(
    dialogState: MutableState<Boolean>, viewModel: CalendarViewModel
) {
    Row(
        modifier = Modifier.clickable { dialogState.value = !dialogState.value },
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_calendar), contentDescription = "연/월 선택"
        )
        Spacer(Modifier.width(2.dp))
        PrText(
            text = viewModel.date.observeAsState().value ?: "", style = TextStyle(
                fontWeight = FontWeight.W700, fontSize = 12.sp
            )
        )
        Image(
            painter = painterResource(id = R.drawable.icon_arrow_open),
            contentDescription = "연/월 선택"
        )
    }
}


@ExperimentalFoundationApi
@Composable
private fun ComposeCalendar(viewModel: CalendarViewModel) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))
        val days = viewModel.dayList.observeAsState().value!!

        // 처음에 선택된 날은 오늘
        val selected = remember { mutableStateOf(viewModel.today) }
        val spread = remember { mutableStateOf(false) }

        DayOfWeekStartsOnMonday()
        Spacer(Modifier.height(8.dp))

        CalendarGrid(days, spread, selected, viewModel)
        Spacer(modifier = Modifier.height(12.dp))

        CalendarSpreadButton(spread)
        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            PrText(
                text = "${selected.value.month}월 ${selected.value.day}일 ${selected.value.getDayOfWeek()}",
                style = TextStyle(
                    color = Color.Black,
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp,
                    letterSpacing = (-0.1).sp
                )
            )
        }
    }
}

@Composable
private fun DayOfWeekStartsOnMonday() {
    LazyHorizontalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        rows = GridCells.Fixed(1),
    ) {
        items(arrayOf("월", "화", "수", "목", "금", "토", "일")) {
            DayOfWeekText(it)
        }
    }
}

@Composable
private fun ColumnScope.CalendarGrid(
    days: MutableList<CalendarDay>,
    spread: MutableState<Boolean>,
    selected: MutableState<CalendarDay>,
    viewModel: CalendarViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(0.dp),
    ) {
        items(days) { date ->
            AnimatedVisibility(visible = spread.value) {
                DayOfMonthIcon(
                    date, selected
                ) {
                    selected.value = it
                }
            }
            AnimatedVisibility(visible = !spread.value) {
                if (date.getWeekOfMonth() == viewModel.weekOfMonth) {
                    DayOfMonthIcon(
                        date, selected
                    ) {
                        selected.value = it
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarSpreadButton(spread: MutableState<Boolean>) {
    Image(
        modifier = Modifier.clickable(
            interactionSource = remember {
                MutableInteractionSource()
            },
            indication = null,
            onClick = { spread.value = !spread.value }
        ),
        painter = if (spread.value) painterResource(id = R.drawable.icon_bigarrow_end)
        else painterResource(id = R.drawable.icon_bigarrow_open),
        contentDescription = "닫기"
    )
}

@Composable
fun DayOfWeekText(text: String) {
    val config = LocalConfiguration.current
    val itemWidth = (config.screenWidthDp.dp - (28.dp * 2)) / 7
    PrText(
        text = text, modifier = Modifier
            .width(itemWidth)
            .height(12.dp), style = TextStyle(
            fontWeight = FontWeight.W700, fontSize = 10.sp, textAlign = TextAlign.Center
        )
    )
}


@SuppressLint("InflateParams")
@Composable
private fun YearMonthDialog(dialogState: MutableState<Boolean>, viewModel: CalendarViewModel) {
    if (dialogState.value) {
        Dialog(onDismissRequest = {
            dialogState.value = false
        }) {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.year_month_picker, null)

                val yearPicker = view.findViewById<NumberPicker>(R.id.picker_year)
                val monthPicker = view.findViewById<NumberPicker>(R.id.picker_month)
                val cancelButton = view.findViewById<Button>(R.id.btn_cancel)
                val confirmButton = view.findViewById<Button>(R.id.btn_confirm)

                yearPicker.minValue = 2023
                yearPicker.maxValue = 2099
                yearPicker.value = viewModel.year()

                monthPicker.minValue = 1
                monthPicker.maxValue = 12
                monthPicker.value = viewModel.month()

                cancelButton.setOnClickListener {
                    dialogState.value = false
                }

                confirmButton.setOnClickListener {
                    viewModel.setDate(yearPicker.value, monthPicker.value - 1)
                    dialogState.value = false
                }

                view
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ComposeCalendarPreview() {
    ComposeCalendar(CalendarViewModel())
}

@Preview(showBackground = true)
@Composable
fun AppLogoPreview() {
    AppLogoImage()
}

@Preview(showBackground = true)
@Composable
fun YearMonthSelectBoxPreview() {
    YearMonthSelectBox(
        dialogState = remember { mutableStateOf(false) }, viewModel = CalendarViewModel()
    )
}

@Preview(showBackground = true)
@Composable
fun YearMonthDialogPreview() {
    YearMonthDialog(
        dialogState = remember { mutableStateOf(true) }, viewModel = CalendarViewModel()
    )
}
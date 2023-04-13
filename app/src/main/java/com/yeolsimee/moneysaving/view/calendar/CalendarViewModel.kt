package com.yeolsimee.moneysaving.view.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeolsimee.moneysaving.domain.calendar.CalendarDay
import com.yeolsimee.moneysaving.domain.calendar.getWeekDays
import com.yeolsimee.moneysaving.domain.calendar.setNextDay
import com.yeolsimee.moneysaving.domain.usecase.RoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val routineUseCase: RoutineUseCase) : ViewModel() {

    private var lastDayOfMonth: Int
    private val calendar = Calendar.getInstance()

    private var _today: CalendarDay
    val today: CalendarDay
        get() = _today

    private val _dayList: MutableLiveData<MutableList<CalendarDay>>
    val dayList: LiveData<MutableList<CalendarDay>>
        get() = _dayList


    init {
        calendar.firstDayOfWeek = Calendar.MONDAY
        val todayCalendar = Calendar.getInstance()
        todayCalendar.time = Date()

        _today = CalendarDay(
            todayCalendar.get(Calendar.YEAR),
            todayCalendar.get(Calendar.MONTH) + 1,
            todayCalendar.get(Calendar.DATE),
            true
        )
        lastDayOfMonth = todayCalendar.getMaximum(Calendar.DAY_OF_MONTH)

        _dayList = MutableLiveData(getWeekDays(calendar))
        val days = _dayList.value!!

        viewModelScope.launch {
            routineUseCase.findAllMyRoutineDays(days.first().toString(), days.last().toString())
        }
    }

    fun year(): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun month(): Int {
        return calendar.get(Calendar.MONTH) + 1
    }

    private val _date: MutableLiveData<String> =
        MutableLiveData("${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월")

    val date: MutableLiveData<String>
        get() = _date

    fun setDate(year: Int, month: Int) {
        calendar.setNextDay(year, month)
        _date.value = "${year}년 ${month + 1}월"
        _dayList.value = getWeekDays(calendar)
    }

    @Suppress("unused")
    fun moveToNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        _dayList.value = getWeekDays(calendar)
        _date.value =
            "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
    }

    @Suppress("unused")
    fun moveToBeforeMonth() {
        calendar.add(Calendar.MONTH, -1)
        _dayList.value = getWeekDays(calendar)
        _date.value =
            "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월"
    }
}
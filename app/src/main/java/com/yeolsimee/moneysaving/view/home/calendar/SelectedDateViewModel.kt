package com.yeolsimee.moneysaving.view.home.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yeolsimee.moneysaving.domain.calendar.CalendarDay
import com.yeolsimee.moneysaving.domain.entity.routine.RoutinesOfDay
import com.yeolsimee.moneysaving.domain.usecase.RoutineUseCase
import com.yeolsimee.moneysaving.ui.side_effect.IToastSideEffect
import com.yeolsimee.moneysaving.ui.side_effect.ToastSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SelectedDateViewModel @Inject constructor(private val routineUseCase: RoutineUseCase) :
    ContainerHost<RoutinesOfDay, ToastSideEffect>, IToastSideEffect, ViewModel() {

    override val container = container<RoutinesOfDay, ToastSideEffect>(RoutinesOfDay())

    fun find(calendarDay: CalendarDay) = intent {
        reduce { RoutinesOfDay("loading") }

        viewModelScope.launch {
            val result = routineUseCase.findRoutineDay(calendarDay.toString())
            result.onSuccess {
                reduce { it }
            }.onFailure {
                showSideEffect(it.message)
            }
        }
    }

    fun refresh(routinesOfDay: RoutinesOfDay) = intent {
        reduce { RoutinesOfDay("loading") }
        delay(100)
        reduce { routinesOfDay }
    }

    override fun showSideEffect(message: String?) {
        intent {
            reduce { RoutinesOfDay() }
            postSideEffect(ToastSideEffect.Toast(message ?: "Unknown Error Message"))
        }
    }
}
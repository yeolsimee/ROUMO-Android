package com.yeolsimee.moneysaving.view.home

import androidx.lifecycle.ViewModel
import com.yeolsimee.moneysaving.data.repository.SettingsRepository
import com.yeolsimee.moneysaving.domain.entity.routine.Routine
import com.yeolsimee.moneysaving.domain.entity.routine.RoutineCheckRequest
import com.yeolsimee.moneysaving.domain.entity.routine.RoutinesOfDay
import com.yeolsimee.moneysaving.domain.usecase.RoutineUseCase
import com.yeolsimee.moneysaving.ui.side_effect.IToastSideEffect
import com.yeolsimee.moneysaving.ui.side_effect.ToastSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class RoutineCheckViewModel @Inject constructor(
    private val routineUseCase: RoutineUseCase,
    private val settingsRepository: SettingsRepository
) :
    ContainerHost<RoutinesOfDay, ToastSideEffect>, IToastSideEffect, ViewModel() {

    override val container = container<RoutinesOfDay, ToastSideEffect>(RoutinesOfDay())

    fun check(
        routineCheckRequest: RoutineCheckRequest,
        routine: Routine,
        refresh: (RoutinesOfDay) -> Unit
    ) = intent {
        val result = routineUseCase.routineCheck(routineCheckRequest)
        result.onSuccess { data ->
            val checked = routineCheckRequest.routineCheckYN == "Y"
            if (checked) {
                settingsRepository.setAlarmOffOnce(routine).collect {
                    reduce {
                        refresh(data)
                        data
                    }
                }
            } else {
                settingsRepository.setAlarmOn(routine).collect {
                    reduce {
                        refresh(data)
                        data
                    }
                }
            }
        }.onFailure { showSideEffect(it.message) }
    }

    override fun showSideEffect(message: String?) {
        intent {
            reduce { RoutinesOfDay() }
            postSideEffect(ToastSideEffect.Toast(message ?: "Unknown Error Message"))
        }
    }
}
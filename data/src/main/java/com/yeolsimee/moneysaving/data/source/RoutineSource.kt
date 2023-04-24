package com.yeolsimee.moneysaving.data.source

import com.yeolsimee.moneysaving.data.api.RoutineApiService
import com.yeolsimee.moneysaving.domain.entity.ApiResponse
import com.yeolsimee.moneysaving.domain.entity.routine.RoutineDays
import com.yeolsimee.moneysaving.domain.entity.routine.RoutineRequest
import com.yeolsimee.moneysaving.domain.entity.routine.RoutineResponse
import com.yeolsimee.moneysaving.domain.entity.routine.RoutinesOfDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RoutineSource(
    private val api: RoutineApiService
) {
    fun findAllMyRoutineDays(startDate: String, endDate: String): Flow<Response<ApiResponse<RoutineDays>>> =
        flow {

            emit(api.findAllMyRoutineDays(startDate, endDate))
        }

    fun findRoutineDay(date: String): Flow<Response<ApiResponse<RoutinesOfDay?>>> = flow {
        emit(api.findRoutineDay(date))
    }

    fun createRoutine(routineRequest: RoutineRequest): Flow<Response<ApiResponse<RoutineResponse>>> = flow {
        emit(api.createRoutine(routineRequest))
    }
}
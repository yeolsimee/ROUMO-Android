package com.yeolsimee.moneysaving.data.source

import com.yeolsimee.moneysaving.data.api.RoutineApiService
import com.yeolsimee.moneysaving.domain.entity.ApiResponse
import com.yeolsimee.moneysaving.domain.entity.routine.RoutineDays
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
}
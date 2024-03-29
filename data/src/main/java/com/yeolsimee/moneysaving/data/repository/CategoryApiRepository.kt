package com.yeolsimee.moneysaving.data.repository

import com.yeolsimee.moneysaving.data.api.CategoryApiService
import com.yeolsimee.moneysaving.data.entity.CategoryEntity
import com.yeolsimee.moneysaving.domain.entity.category.CategoryIdRequest
import com.yeolsimee.moneysaving.domain.entity.category.CategoryNameRequest
import com.yeolsimee.moneysaving.domain.entity.category.CategoryOrderChangeRequest
import com.yeolsimee.moneysaving.domain.entity.category.TextItem
import com.yeolsimee.moneysaving.domain.exception.ApiException
import com.yeolsimee.moneysaving.domain.repository.ICategoryApiRepository
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single

class CategoryApiRepository(private val api: CategoryApiService) : ICategoryApiRepository {
    override suspend fun getCategoryList(): Result<MutableList<TextItem>> {
        val response = flow { emit(api.getCategoryList()) }.single()
        val result = response.body()

        return if (result != null && result.hasData()) {
            Result.success(result.data.map { it.toTextItem() }.toMutableList())
        } else {
            Result.failure(ApiException(response.code(), result?.message))
        }
    }

    override suspend fun addCategory(name: String): Result<TextItem> {
        val response = flow { emit(api.addCategory(CategoryNameRequest(name))) }.single()
        val result = response.body()

        return if (result != null && result.hasData()) {
            Result.success(result.data.toTextItem())
        } else {
            Result.failure(ApiException(response.code(), result?.message))
        }
    }

    override suspend fun delete(category: TextItem): Result<Any> {
        val response = flow {
            emit(
                api.deleteCategory(
                    CategoryIdRequest(category.id)
                )
            )
        }.single()
        val result = response.body()

        return if (result != null && result.code == 0) {
            Result.success(true)
        } else {
            Result.failure(ApiException(response.code(), result?.message))
        }
    }

    override suspend fun update(category: TextItem): Result<Any> {
        val response =
            flow { emit(api.updateCategory(CategoryEntity.fromTextItem(category))) }.single()
        val result = response.body()
        return if (result != null && result.hasData()) {
            Result.success(true)
        } else {
            Result.failure(ApiException(response.code(), result?.message))
        }
    }

    override fun changeOrder(categoryOrderChangeRequest: CategoryOrderChangeRequest) = flow {
        val response = api.changeOrder(categoryOrderChangeRequest)
        val result = response.body()
        if (result != null && result.hasData()) {
            emit(Result.success(true))
        } else {
            emit(Result.failure(ApiException(response.code(), result?.message)))
        }
    }
}
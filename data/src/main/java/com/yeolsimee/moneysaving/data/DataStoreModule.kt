package com.yeolsimee.moneysaving.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yeolsimee.moneysaving.data.data_store.DataStoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStoreService(dataStore: DataStore<Preferences>): DataStoreService {
        return object: DataStoreService {
            override fun getDataStore(): DataStore<Preferences> {
                return dataStore
            }
        }
    }

    @Provides
    @Singleton
    internal fun provideSettingPreferences(application: Application): DataStore<Preferences> {
        return application.applicationContext.settingsDataStore
    }
}
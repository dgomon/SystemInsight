package com.dgomon.systeminsight.di

import android.content.Context
import com.dgomon.systeminsight.core.AppInfoProvider
import com.dgomon.systeminsight.core.DefaultAppInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppInfoModule {

    @Provides
    @Singleton
    fun provideAppInfoProvider(
        @ApplicationContext context: Context
    ): AppInfoProvider = DefaultAppInfoProvider(context)
}

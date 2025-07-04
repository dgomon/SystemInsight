package com.dgomon.systeminsight.di

import com.dgomon.systeminsight.presentation.shizuku.ShizukuServiceManager
import com.dgomon.systeminsight.core.service.PrivilegedServiceConnectionProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceBindingModule {

    @Binds
    abstract fun bindPrivilegedServiceConnectionProvider(
        impl: ShizukuServiceManager
    ): PrivilegedServiceConnectionProvider
}

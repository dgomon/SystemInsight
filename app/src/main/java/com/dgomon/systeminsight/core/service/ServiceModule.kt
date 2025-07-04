package com.dgomon.systeminsight.core.service

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {

    @Binds
    fun bindCommandServiceClient(
        impl: CommandServiceClientImpl
    ): CommandServiceClient
}

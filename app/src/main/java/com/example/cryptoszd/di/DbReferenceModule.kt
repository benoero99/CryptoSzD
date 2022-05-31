package com.example.cryptoszd.di

import com.example.cryptoszd.network.DbReference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbReferenceModule {
    @Provides
    @Singleton
    fun provideCollection() = DbReference.Collection

    @Provides
    @Singleton
    fun provideFields() = DbReference.Field
}
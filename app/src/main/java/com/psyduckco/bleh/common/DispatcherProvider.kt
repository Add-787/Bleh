package com.psyduckco.bleh.common

import kotlin.coroutines.CoroutineContext

interface DispatcherProvider {
    fun provideUIContext() : CoroutineContext
    fun provideIOContext() : CoroutineContext
}
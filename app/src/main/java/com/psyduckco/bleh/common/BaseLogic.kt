package com.psyduckco.bleh.common

import kotlinx.coroutines.Job

abstract class BaseLogic<T> {
    protected lateinit var jobTracker: Job
    abstract  fun onEvent(event: T)
}
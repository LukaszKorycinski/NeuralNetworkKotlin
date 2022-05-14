package com.example.neuralnetworkkotlin.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


/**
 * Helper to create MutableLiveData with some default value.
 */
fun <T> mutableLiveData(defaultValue: T?) =
    MutableLiveData<T>().apply { value = defaultValue }

/**
 * Helper that ignores null values from LiveData and delivers only non null values to the observer.
 */
fun <T: Any> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (T) -> Unit) =
    observe(owner, Observer { it?.let(observer) })
package com.jc.topstackoverflowusers.presentation.mapper

import com.jc.topstackoverflowusers.presentation.model.ErrorType
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toErrorType(): ErrorType {
    return when (this) {
        is IOException -> ErrorType.NETWORK
        is HttpException -> ErrorType.SERVER
        else -> ErrorType.GENERIC
    }
}
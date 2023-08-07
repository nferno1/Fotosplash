package nferno1.fotosplash.api

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

class UnsplashResponseHandler @Inject constructor() {

    fun <T : Any> handleSuccess(data: T? = null): UnsplashResource.Success<T> {
        return UnsplashResource.Success(data)
    }

    fun <T : Any> handleException(e: Exception): UnsplashResource.Error<T> {
        return when (e) {
            is ConnectException -> {
                UnsplashResource.Error("Connection Error")
            }

            is HttpException -> {
                val res = e.response()?.errorBody()?.string()?.trim()
                UnsplashResource.Error(res.toString())
            }

            is SocketTimeoutException -> {
                UnsplashResource.Error("Timeout")
            }

            else -> {
                UnsplashResource.Error(e.message.toString())
            }
        }
    }

}
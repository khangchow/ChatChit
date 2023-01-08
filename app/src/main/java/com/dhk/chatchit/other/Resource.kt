package com.dhk.chatchit.other

//data class Resource<out T>(val status: Status, val data: T?) {
//    companion object {
//        fun <T> success(data: T? = null): Resource<T> {
//            return Resource(Status.SUCCESS, data)
//        }
//
//        fun <T> error(): Resource<T> {
//            return Resource(Status.ERROR, null)
//        }
//
//        fun <T> loading(): Resource<T> {
//            return Resource(Status.LOADING, null)
//        }
//    }
//}
//
//enum class Status {
//    SUCCESS,
//    ERROR,
//    LOADING
//}

sealed class Resource<T> {
    class Success<T>(val data: T? = null) : Resource<T>()
    class Error<T>(val data: T? = null) : Resource<T>()
    object Loading: Resource<Nothing>()
}
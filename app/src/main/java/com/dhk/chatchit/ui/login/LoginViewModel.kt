package com.dhk.chatchit.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhk.chatchit.other.Event
import com.dhk.chatchit.other.Resource
import com.dhk.chatchit.other.validator.Validator

class LoginViewModel : ViewModel() {
    private val _loginStatus = MutableLiveData<Event<Resource<String>>>()
    val loginStatus = _loginStatus

    fun login(username: String) {
        if (Validator.isUsernameValid(username)) _loginStatus.postValue(Event(Resource.Success(username)))
        else _loginStatus.postValue(Event(Resource.Error()))
    }
}
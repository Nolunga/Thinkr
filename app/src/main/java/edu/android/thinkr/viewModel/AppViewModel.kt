package edu.android.thinkr.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.google.android.material.textfield.TextInputEditText
import edu.android.thinkr.repository.FirebaseRepo
import edu.android.thinkr.user.User
import edu.android.thinkr.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author robin
 * Created on 10/10/20
 */

public class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseRepository = FirebaseRepo.getInstance()


    fun registerUser(email: String, password: String): LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.registerUser(email, password))
        }
        return genericLiveData
    }

    fun putUser(user: User) : LiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.putUser(user))
        }
        return genericLiveData
    }

    fun loginUser(email : String, password: String): LiveData<Resource<String>>  {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.signInUsers(email, password))
        }
        return genericLiveData
    }

    fun resetPassword(email: String): MutableLiveData<Resource<String>> {
        val genericLiveData = MutableLiveData<Resource<String>>()
        genericLiveData.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            genericLiveData.postValue(firebaseRepository.resetPassword(email))
        }
        return genericLiveData
    }
}

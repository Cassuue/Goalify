package ca.uqac.goalify.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    private val _profileName = MutableLiveData<String>()
    val profileName: LiveData<String> get() = _profileName

    private val _profileEmail = MutableLiveData<String>()
    val profileEmail: LiveData<String> get() = _profileEmail

    fun updateProfile(name: String, email: String) {
        _profileName.value = name
        _profileEmail.value = email
    }
}

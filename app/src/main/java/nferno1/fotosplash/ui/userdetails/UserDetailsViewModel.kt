package nferno1.fotosplash.ui.userdetails

import androidx.lifecycle.*
import nferno1.fotosplash.data.Results
import nferno1.fotosplash.data.collections.Collections
import nferno1.fotosplash.data.me.Me
import nferno1.fotosplash.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val repository: UnsplashRepository,
) : ViewModel() {

    private val _user = MutableLiveData<Me>().apply {
        value = Me()
    }
    val user: LiveData<Me> = _user

    val photo = MutableLiveData<List<Results>?>(emptyList())
    val collections = MutableLiveData<List<Collections>?>(emptyList())

    fun getInfoMe(token: String) {
        viewModelScope.launch {
            val res = repository.getMe(token)
            if (res != null) {
                _user.value = res
            }
        }
    }

    fun getDownload(id: String, token: String) {
        viewModelScope.launch {
            repository.getDownload(id, token)
        }
    }

    fun getPhoto(username: String, token: String, items: String = "likes") {
        viewModelScope.launch {
            val res = repository.getMeLikes(username, token, items)
            if (res != null) photo.value = res
        }
    }

    fun getCollections(username: String, token: String) {
        viewModelScope.launch {
            collections.value = repository.getMeCollections(username, token)
        }
    }

    fun likedOut(id: String, token: String) {
        val itemRemoveFromDB = photo.value?.find { it.id == id }

        viewModelScope.launch {
            repository.likedFromMyLikesPhoto(id, token, itemRemoveFromDB)
        }
    }

    fun deleteAllInfo() {
        viewModelScope.launch {
            repository.allDeleteInDB()
        }
    }

}
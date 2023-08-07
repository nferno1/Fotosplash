package nferno1.fotosplash.ui.collectionsOpen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import nferno1.fotosplash.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionsOpenViewModel @Inject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData<CurrentTokenID>()

    var collectionsPhotos = currentQuery.switchMap {
        Log.d("collectionsBind", it.toString())
        repository.getPhotoFromCollection(it.id, it.token)
    }

    fun getDownload(id: String, token: String) {
        viewModelScope.launch {
            repository.getDownload(id, token)
        }
    }

    fun getPhoto(id: String, token: String) {
        currentQuery.value = CurrentTokenID(id, token)
    }

    fun liked(id: String, token: String, b: Boolean) {
        viewModelScope.launch {
            repository.liked(id, token, b)
        }
    }

}

data class CurrentTokenID(
    val id: String,
    val token: String
)
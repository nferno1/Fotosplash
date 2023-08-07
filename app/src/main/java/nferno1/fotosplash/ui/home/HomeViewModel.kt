package nferno1.fotosplash.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import nferno1.fotosplash.repository.UnsplashRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData<Authorization>()

    val photos = currentQuery.switchMap {
        repository.getSearchResults(it.query, it.token).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String, token: String) {
        currentQuery.value = Authorization(query, token)
    }

    fun liked(id: String, token: String, b: Boolean) {
        viewModelScope.launch {
            repository.liked(id, token, b)
        }
    }
}

data class Authorization(
    val query: String,
    val token: String,

    )
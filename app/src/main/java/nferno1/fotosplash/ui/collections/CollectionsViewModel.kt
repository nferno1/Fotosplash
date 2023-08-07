package nferno1.fotosplash.ui.collections

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import nferno1.fotosplash.repository.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val repository: UnsplashRepository
) : ViewModel() {

    private val currentQuery = MutableLiveData<String>()

    var collections = currentQuery.switchMap {
        repository.getCollections(it)
    }

    fun getCollections(token: String) {
        currentQuery.value = token
    }

}
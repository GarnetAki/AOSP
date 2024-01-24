package com.example.images_garnetaki;

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class Album(
    val name: String = "",
    val size: Int = 0,
    val id: Int = 0,
    val img: String = "",
)

data class AlbumsUiState(
    val albums: MutableList<Album> = emptyList<Album>().toMutableList(),
    val numberOfAlbums: Int = 0,
)

class AlbumsViewModel(private val repository: AlbumRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    fun upload(
        uris: List<@JvmSuppressWildcards Uri>,
        albumId: Int,
        contentResolver: ContentResolver,
        userId: Long
    ) {
        viewModelScope.launch{
            val uploadPhotosFunc = async { repository.uploadPhotos(uris, albumId, contentResolver) }
            val str = uploadPhotosFunc.await()
            if (str == "success")
                update(userId)
        }
    }

    fun update(userId: Long) {
        viewModelScope.launch{
            val getAlbumsFunc = async { repository.getAlbums(userId) }
            val albums = getAlbumsFunc.await()
            _uiState.update { currentState ->
                currentState.copy(
                    numberOfAlbums = albums.size,
                    albums = albums,
                )
            }
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun modelFactory(repository: AlbumRepository)
                : ViewModelProvider.Factory =object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlbumsViewModel(repository) as T
            }
        }
    }
}

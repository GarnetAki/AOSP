package com.example.images_garnetaki

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val fingerprints = "75:37:25:14:6E:00:CC:0A:28:88:35:E5:8F:7C:26:CE:3E:35:AF:2F";

    private lateinit var loginButton : ImageButton

    private lateinit var logoutButton : ImageButton

    private lateinit var albumButton : ImageButton

    private lateinit var loginData : TextView

    private lateinit var recyclerView: RecyclerView

    var albumId: Int = 0

    private var viewModel: AlbumsViewModel = AlbumsViewModel.modelFactory(AlbumRepository())
        .create(AlbumsViewModel::class.java)

    private var userId : Long = 0

    private val VK_APP_ID : Int = 51837375

    private val VK_SCOPES = arrayListOf(VKScope.STATUS, VKScope.PHOTOS)

    val authLauncher = VK.login(this) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                loginData.text = getString(R.string.id).plus(result.token.userId.value.toString())
                userId = result.token.userId.value
            }
            is VKAuthenticationResult.Failed -> {
                loginData.text = getString(R.string.empty_id)
            }
        }
    }

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            buttonClicked(logoutButton)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
            RequestPermission()
        ) { isGranted ->
            when {
                isGranted -> {
                    pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
                }
                else -> {
                    println("Denied")
                }
            }
        }

    fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                getContext(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) -> {
                pickMultipleMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }

    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isNotEmpty()) {
                viewModel.upload(uris, albumId, contentResolver, userId)

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        loginButton = findViewById(R.id.VKLogin)
        logoutButton = findViewById(R.id.VKLogout)
        albumButton = findViewById(R.id.FindAlbums)
        loginData = findViewById(R.id.login_text)
        recyclerView = findViewById(R.id.recyclerView)

        VK.addTokenExpiredHandler(tokenTracker)
        userId = VK.getUserId().value
        if (userId != 0.toLong()){
            loginData.text = getString(R.string.id).plus(VK.getUserId())
            buttonClicked(albumButton)
        }else{
            loginData.text = getString(R.string.empty_id)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                buttonClicked(albumButton)
                viewModel.uiState.collect {
                    recyclerView.setHasFixedSize(true)
                    recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                    val albumListAdapter = AlbumListAdapter(getContext(), it.albums)
                    recyclerView.adapter = albumListAdapter
                }
            }
        }
    }

    fun buttonClicked(view: View) {
        when (view.id) {
            R.id.VKLogin -> {
                authLauncher.launch(VK_SCOPES)
                viewModel.update(userId)
            }
            R.id.VKLogout -> {
                VK.logout()
                loginData.text = getString(R.string.empty_id)
                userId = 0
                viewModel.update(userId)
            }
            R.id.FindAlbums -> {
                viewModel.update(userId)
            }
        }
    }

    private fun getContext(): MainActivity {
        return this
    }
}

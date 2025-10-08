package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.denica.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistFragment
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistViewModel
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import com.denica.playlistmaker.utils.BindingFragment
import java.io.File
import java.io.FileOutputStream

abstract class BasePlaylistFragment<VM : CreatePlaylistViewModel> :
    BindingFragment<FragmentCreatePlaylistBinding>() {

    protected abstract val viewModel: VM


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }


    protected open fun setupListeners() {

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(binding.createPlaylistImage)
                        .load(uri)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(
                                TrackListViewHolder.dpToPx(
                                    8f,
                                    binding.createPlaylistImage.context
                                )
                            )
                        ).apply(
                            RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(
                                    true
                                ))
                        .into(binding.createPlaylistImage)
                    viewModel.pickImage(uri)

                }
            }

        binding.createPlaylistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistNameEditText.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.createPlaylistButtonCreate.isEnabled = !s.isNullOrEmpty()
                }

                override fun afterTextChanged(s: Editable?) {}
            }
        )


    }

    protected open fun isShowAlert(): Boolean {
        return binding.createPlaylistNameEditText.text?.isNotEmpty() ?: false
    }


    protected fun saveToInternal(fileName: String, uri: Uri): String {
        val file =
            File(
                requireContext().getDir(
                    CreatePlaylistFragment.Companion.PLAYLIST_IMAGES_DIR_NAME,
                    Context.MODE_PRIVATE
                ),
                "$fileName.jpg"
            )
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.absolutePath
    }

}
package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistFragment
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistViewModel
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : CreatePlaylistFragment() {

    val args by navArgs<EditPlaylistFragmentArgs>()
    override lateinit var viewModel: CreatePlaylistViewModel
    lateinit var viewmodelEdit: EditPlaylistViewmodel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        val playlist = args.playlist
        viewModel = getViewModel<EditPlaylistViewmodel>(parameters = { parametersOf(playlist) })
        viewmodelEdit = viewModel as EditPlaylistViewmodel
        binding.createPlaylistHeader.title = getString(R.string.playlist_header_edit_edit_fragment)
        binding.createPlaylistButtonCreate.text =
            getString(R.string.save_edit_playlist_button_edit_fragment)
        viewmodelEdit.getEditPlaylistState().observe(viewLifecycleOwner) {
            Glide.with(binding.createPlaylistImage).load(it.playlistImagePath)
                .placeholder(R.drawable.ic_add_photo_create_playlist)
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
                        )
                ).into(binding.createPlaylistImage)
            binding.createPlaylistNameEditText.setText(it.playlistName)
            binding.createPlaylistDescriptionEditText.setText(it.playlistDescription)
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            })
        binding.createPlaylistHeader.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.createPlaylistButtonCreate.setOnClickListener {
            val (picked, uri) = viewmodelEdit.isImagePicked().value ?: Pair(false, "")
            val nameEd = binding.createPlaylistNameEditText.text.toString()
            val descriptionEd = binding.createPlaylistDescriptionEditText.text.toString()

            if (picked) {
                val savedPath = saveToInternal(nameEd, uri.toUri())
                viewmodelEdit.updatePlaylist(nameEd, descriptionEd, savedPath)

                if (playlist.name != nameEd) {
                    deleteOldFile(playlist.name)
                }
            } else {
                viewmodelEdit.updatePlaylist(nameEd, descriptionEd, playlist.imagePath)
            }

            findNavController().navigateUp()
        }

    }


    fun deleteOldFile(oldFileName: String) {
        val file = File(
            requireContext().getDir(PLAYLIST_IMAGES_DIR_NAME, Context.MODE_PRIVATE),
            "$oldFileName.jpg"
        )
        file.delete()
    }

}
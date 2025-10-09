package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist.CreatePlaylistFragment.Companion.PLAYLIST_IMAGES_DIR_NAME
import com.denica.playlistmaker.mediaLibrary.ui.playlist.playlistdetail.PlaylistDetailFragment
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class EditPlaylistFragment : BasePlaylistFragment<EditPlaylistViewmodel>() {

    val args by navArgs<EditPlaylistFragmentArgs>()
    override lateinit var viewModel: EditPlaylistViewmodel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        val playlist = args.playlist
        viewModel = getViewModel<EditPlaylistViewmodel>(parameters = { parametersOf(playlist) })
        binding.createPlaylistHeader.title = getString(R.string.playlist_header_edit_edit_fragment)
        binding.createPlaylistButtonCreate.text =
            getString(R.string.save_edit_playlist_button_edit_fragment)
        viewModel.getEditPlaylistState().observe(viewLifecycleOwner) {
            PlaylistDetailFragment.setGlide(
                it.playlistImagePath, binding.createPlaylistImage,
                MultiTransformation(
                    CenterCrop(),
                    RoundedCorners(
                        TrackListViewHolder.dpToPx(
                            8f,
                            binding.createPlaylistImage.context
                        )
                    )
                ), R.drawable.ic_add_photo_create_playlist
            )
            binding.createPlaylistNameEditText.setText(it.playlistName)
            binding.createPlaylistDescriptionEditText.setText(it.playlistDescription)
        }
        binding.createPlaylistHeader.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.createPlaylistButtonCreate.setOnClickListener {
            val (picked, uri) = viewModel.isImagePicked().value ?: Pair(false, "")
            val nameEd = binding.createPlaylistNameEditText.text.toString()
            val descriptionEd = binding.createPlaylistDescriptionEditText.text.toString()

            if (picked) {
                val savedPath = saveToInternal(nameEd, uri.toUri())
                viewModel.updatePlaylist(nameEd, descriptionEd, savedPath)

                if (playlist.name != nameEd) {
                    deleteOldFile(playlist.name)
                }
            } else {
                viewModel.updatePlaylist(nameEd, descriptionEd, playlist.imagePath)
            }

            findNavController().navigateUp()
        }

    }


    private fun deleteOldFile(oldFileName: String) {
        val file = File(
            requireContext().getDir(PLAYLIST_IMAGES_DIR_NAME, Context.MODE_PRIVATE),
            "$oldFileName.jpg"
        )
        file.delete()
    }

}
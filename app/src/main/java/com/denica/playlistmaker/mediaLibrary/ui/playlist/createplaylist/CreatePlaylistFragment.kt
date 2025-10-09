package com.denica.playlistmaker.mediaLibrary.ui.playlist.createplaylist

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.denica.playlistmaker.mediaLibrary.ui.playlist.BasePlaylistFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.getViewModel

class CreatePlaylistFragment : BasePlaylistFragment<CreatePlaylistViewModel>() {

    override lateinit var viewModel: CreatePlaylistViewModel
    lateinit var alert: MaterialAlertDialogBuilder
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            if (isShowAlert()) {
                alert.show()

            } else findNavController().navigateUp()

        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel<CreatePlaylistViewModel>()
        alert = setupAlert()
        binding.createPlaylistHeader.setNavigationOnClickListener {
            Log.d("isShowAlert", isShowAlert().toString())
            if (isShowAlert()) {
                alert.show()
            } else {
                findNavController().navigateUp()
            }
        }




        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, callback
        )



        binding.createPlaylistButtonCreate.setOnClickListener {
            createPlaylist()
            findNavController().navigateUp()
        }
    }

    fun setupAlert(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertTheme)
            .setTitle(getString(R.string.create_playlist_alert_title))
            .setMessage(getString(R.string.create_playlist_alert_message))
            .setNeutralButton(getString(R.string.create_playlist_alert_neutral_button_text)) { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.create_playlist_alert_positive_button)) { dialog, which ->
                createPlaylist()
                findNavController().navigateUp()
            }
    }
    private fun createPlaylist() {
        if (isShowAlert()) {
            val name = binding.createPlaylistNameEditText.text.toString()
            viewModel.isImagePicked().observe(viewLifecycleOwner) {
                viewModel.addPlaylist(
                    name,
                    binding.createPlaylistDescriptionEditText.text.toString(),
                    if (it.second != "") {
                        saveToInternal(name, it.second.toUri())
                    } else Uri.Builder()
                        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                        .authority(resources.getResourcePackageName(R.drawable.ic_track_placeholder))
                        .appendPath(resources.getResourceTypeName(R.drawable.ic_track_placeholder))
                        .appendPath(resources.getResourceEntryName(R.drawable.ic_track_placeholder))
                        .build().toString()
                )
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.create_playlist_create_succes, name), Toast.LENGTH_SHORT
            )
                .show()

        }
    }


    companion object {

        const val PLAYLIST_IMAGES_DIR_NAME = "playlistImages"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePlaylistFragment().apply {

            }
    }
}
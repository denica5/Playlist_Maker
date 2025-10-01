package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.denica.playlistmaker.search.ui.TrackListViewHolder
import com.denica.playlistmaker.utils.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue


class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {
    val viewModel by viewModel<CreatePlaylistViewModel>()
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
        alert = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.create_playlist_alert_title))
            .setMessage(getString(R.string.create_playlist_alert_message))
            .setNeutralButton(getString(R.string.create_playlist_alert_neutral_button_text)) { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.create_playlist_alert_positive_button)) { dialog, which ->
                createPlaylist()
                findNavController().navigateUp()
            }
        binding.createPlaylistHeader.setNavigationOnClickListener {
            Log.d("isShowAlert", isShowAlert().toString())
            if (isShowAlert()) {
                alert.show()
            } else {
                findNavController().navigateUp()
            }
        }
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(binding.createPlaylistImage).load(uri)

                        .centerCrop()
                        .transform(
                            RoundedCorners(
                                TrackListViewHolder.dpToPx(
                                    8f,
                                    binding.createPlaylistImage.context
                                )
                            )
                        ).into(binding.createPlaylistImage)
                    viewModel.pickImage(uri)
                } else {
//                    Toast.makeText(requireContext(), R.string.failed_search, Toast.LENGTH_SHORT)
//                        .show()
                }

            }



        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, callback
        )
        binding.createPlaylistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        val nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                binding.createPlaylistButtonCreate.isEnabled = s?.isNotEmpty() ?: false
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        binding.createPlaylistNameEditText.addTextChangedListener(nameTextWatcher)
        binding.createPlaylistButtonCreate.setOnClickListener {
            createPlaylist()
            findNavController().navigateUp()
        }
    }

    private fun isShowAlert(): Boolean {
        var isPicked = false
        viewModel.isImagePicked().observe(viewLifecycleOwner) {
            isPicked = it.first
        }

        return isPicked && binding.createPlaylistNameEditText.text?.isNotEmpty() ?: false
    }

    private fun createPlaylist() {
        if (isShowAlert()) {
            val name = binding.createPlaylistNameEditText.text.toString()
            viewModel.isImagePicked().observe(viewLifecycleOwner) {
                viewModel.addPlaylist(
                    name,
                    binding.createPlaylistDescriptionEditText.text.toString(),
                    saveToInternal(name, it.second)
                )
            }
        }
    }


private fun saveToInternal(fileName: String, uri: Uri): String {
    val file =
        File(requireContext().getDir("playlistImages", Context.MODE_PRIVATE), "$fileName.jpg")
    val inputStream = requireContext().contentResolver.openInputStream(uri)
    val outputStream = FileOutputStream(file)
    BitmapFactory.decodeStream(inputStream)
        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    return file.absolutePath
}

companion object {

    @JvmStatic
    fun newInstance(param1: String, param2: String) =
        CreatePlaylistFragment().apply {

        }
}
}
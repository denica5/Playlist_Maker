package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.denica.playlistmaker.utils.BindingFragment


class CreatePlaylistFragment : BindingFragment<FragmentCreatePlaylistBinding>() {


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container,false)
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
        binding.createPlaylistHeader.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.createPlaylistButtonCreate.isEnabled = true
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePlaylistFragment().apply {

            }
    }
}
package com.denica.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denica.playlistmaker.databinding.FragmentFavouriteTracksBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class FavouriteTracksFragment : BindingFragment<FragmentFavouriteTracksBinding>() {

    val viewModel by viewModel<FavouriteTracksViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            FavouriteTracksFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
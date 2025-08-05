package com.denica.playlistmaker.mediaLibrary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denica.playlistmaker.BindingFragment
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaLibraryFragment : BindingFragment<FragmentMediaLibraryBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaLibraryBinding {
        return FragmentMediaLibraryBinding.inflate(inflater, container, false)
    }

    private var _tabMediator: TabLayoutMediator? = null
    private val tabMediator: TabLayoutMediator get() = _tabMediator!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)
        tabMediator.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _tabMediator =  TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.media_library_favourite_tracks_label)
                1 -> tab.text = getString(R.string.media_library_playlists_label)
            }
        }

        return binding.root
    }


    override fun onDestroyView() {
        tabMediator.detach()
        _tabMediator = null
        super.onDestroyView()
    }
}
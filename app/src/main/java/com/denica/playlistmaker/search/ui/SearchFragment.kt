package com.denica.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.denica.playlistmaker.BindingFragment
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.FragmentSearchBinding
import com.denica.playlistmaker.search.domain.models.Song
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    private var searchText = ""
    val viewModel by viewModel<SearchViewModel>()
    private var isClickAllowed = true
    private lateinit var adapter: TrackListAdapter
    private lateinit var historyAdapter: TrackListAdapter
    private val handler = Handler(Looper.getMainLooper())
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        
        val itemClickListener = object : OnItemClickListener {
            override fun onItemClick(song: Song) {
                if (clickDebounce()) {
                    val position = viewModel.addTrack(song)
//                    startActivity(
//                        Intent(
//                            this@SearchActivity,
//                            MediaPlayerFragment::class.java
//                        ).apply {
//                            putExtra(TRACK_KEY, song)
//                        })
                    findNavController().navigate(SearchFragmentDirections.actionSearchFragment2ToMediaPlayerFragment(song))
                    if (position != -1) {
                        historyAdapter.notifyItemMoved(position, 0)
                        binding.trackListRc.scrollToPosition(0)
                    }
                }
            }
        }
        adapter = TrackListAdapter(itemClickListener)
        historyAdapter = TrackListAdapter(itemClickListener)

        viewModel.getSavedTracksArrayList()
            .observe(viewLifecycleOwner) { historyAdapter.itemList = it }
        binding.searchEditText.setText(searchText)
        binding.searchClearIcX.setOnClickListener {
            binding.searchEditText.setText("")
            try {
                val imm =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
                clearTracks("clear_button")
            } catch (_: Exception) {
                //
            }

        }
        binding.refreshButton.setOnClickListener {
            if (clickDebounce()) {
                viewModel.searchRequest(binding.searchEditText.text.toString())
            }

        }


        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.searchClearIcX.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
                if (searchText != "") {
                    viewModel.searchDebounce(searchText)
                }
                if (viewModel.isSavedTracksArrayListNotEmpty()) {
                    if (binding.searchEditText.hasFocus() && s?.isEmpty() == true) {
                        binding.clearHistoryButton.isVisible = true
                        binding.youSearchTextView.isVisible = true
                        binding.trackListRc.adapter = historyAdapter
                    } else {
                        binding.clearHistoryButton.isVisible = false
                        binding.youSearchTextView.isVisible = false
                        binding.trackListRc.adapter = adapter
                    }
                } else {
                    binding.clearHistoryButton.isVisible = false
                    binding.youSearchTextView.isVisible = false
                    binding.trackListRc.adapter = adapter
                }

            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        binding.searchEditText.addTextChangedListener(searchTextWatcher)
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory()
        }





        binding.trackListRc.adapter = adapter
        binding.trackListRc.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchEditText.text.isNotEmpty()) {
                    viewModel.searchDebounce(binding.searchEditText.text.toString())
                } else {
                    clearTracks(getString(R.string.nothing_found))
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            binding.clearHistoryButton.isVisible = false
            binding.youSearchTextView.isVisible = false

        }
        viewModel.getState()
            .observe(viewLifecycleOwner) {
                when (it) {
                    is SearchState.Loading -> {
                        binding.searchProgressBar.isVisible = true
                        binding.trackListRc.isVisible = false
                        binding.notFoundError.isVisible = false
                        binding.failedSearchError.isVisible = false
                    }

                    is SearchState.Content -> {
                        adapter.itemList = it.data
                        adapter.notifyDataSetChanged()
                        clearTracks("")
                    }

                    is SearchState.Empty -> {
                        clearTracks(getString(it.message))
                    }

                    is SearchState.Error -> {
                        clearTracks(getString(it.message))
                    }
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearTracks(text: String) {

        if (text.isNotEmpty()) {
            adapter.itemList = emptyList()
            adapter.notifyDataSetChanged()
        }
        when (text) {
            getString(R.string.failed_search) -> {
                binding.notFoundError.isVisible = false
                binding.trackListRc.isVisible = false
                binding.failedSearchError.isVisible = true
                binding.searchProgressBar.isVisible = false
            }

            getString(R.string.nothing_found) -> {
                binding.notFoundError.isVisible = true
                binding.trackListRc.isVisible = false
                binding.failedSearchError.isVisible = false
                binding.searchProgressBar.isVisible = false
            }

            "" -> {
                binding.notFoundError.isVisible = false
                binding.trackListRc.isVisible = true
                binding.failedSearchError.isVisible = false
                binding.searchProgressBar.isVisible = false
            }

            "clear_button" -> {
                binding.notFoundError.isVisible = false
                binding.trackListRc.isVisible = true
                binding.failedSearchError.isVisible = false
                binding.searchProgressBar.isVisible = false
            }
        }
    }

    private fun showHistory() {
        if (viewModel.isSavedTracksArrayListNotEmpty()) {
            if (binding.searchEditText.hasFocus() && binding.searchEditText.text.isEmpty()) {
                binding.clearHistoryButton.isVisible = true
                binding.youSearchTextView.isVisible = true
                binding.trackListRc.adapter = historyAdapter
            } else {
                binding.clearHistoryButton.isVisible = false
                binding.youSearchTextView.isVisible = false
                binding.trackListRc.adapter = adapter
            }
        }
    }


    override fun onStop() {
        super.onStop()
        viewModel.saveHistory()
    }

    companion object {
        const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY) ?: ""
//    }


}
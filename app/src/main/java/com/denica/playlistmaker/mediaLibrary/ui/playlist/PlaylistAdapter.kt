package com.denica.playlistmaker.mediaLibrary.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.PlaylistItemBinding
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.mediaplayer.ui.MediaPlayerBottomBehaviorViewHolder
import com.denica.playlistmaker.search.ui.TrackListViewHolder

class PlaylistAdapter(val onItemClickListener: (Playlist) -> Unit) :
    RecyclerView.Adapter<PlaylistViewHolder>() {
    var itemList: List<Playlist> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {

        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PlaylistViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            onItemClickListener(itemList[holder.bindingAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

class PlaylistViewHolder(val binding: PlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(itemView.context).load(playlist.imagePath)
            .placeholder(R.drawable.ic_track_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(TrackListViewHolder.dpToPx(8f, itemView.context))
            )
            .into(binding.playlistImage)



        binding.playlistName.text = playlist.name.trim()
        binding.playlistSongCount.text =
            MediaPlayerBottomBehaviorViewHolder.pluralizeTracks(
                playlist.trackCount,
                itemView.context
            )

    }
}
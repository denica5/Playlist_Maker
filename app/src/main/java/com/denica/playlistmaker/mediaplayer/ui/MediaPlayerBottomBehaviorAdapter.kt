package com.denica.playlistmaker.mediaplayer.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.BottomBehaviorPlaylistItemBinding
import com.denica.playlistmaker.mediaLibrary.domain.Playlist
import com.denica.playlistmaker.search.ui.TrackListViewHolder

class MediaPlayerBottomBehaviorAdapter(val onItemClickListener: (Playlist) -> Unit) :
    RecyclerView.Adapter<MediaPlayerBottomBehaviorViewHolder>() {
    var itemList: List<Playlist> = arrayListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MediaPlayerBottomBehaviorViewHolder {

        val binding =
            BottomBehaviorPlaylistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return MediaPlayerBottomBehaviorViewHolder(binding)
    }


    override fun onBindViewHolder(
        holder: MediaPlayerBottomBehaviorViewHolder,
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

class MediaPlayerBottomBehaviorViewHolder(val binding: BottomBehaviorPlaylistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(itemView.context).load(playlist.imagePath)
            .placeholder(R.drawable.ic_track_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(TrackListViewHolder.dpToPx(2f, itemView.context))
            )
            .into(binding.playlistImage)



        binding.playlistName.text = playlist.name.trim()
        binding.playlistSongCount.text = pluralizeTracks(playlist.trackCount, itemView.context)

    }

    companion object {
        fun pluralizeTracks(tracksCounts: Int, context: Context): String {
            val lastTwo = tracksCounts % 100
            val lastOne = tracksCounts % 10

            return when {
                lastTwo in 11..19 -> context.getString(
                    R.string.playlist_track_pluralize_11_19,
                    tracksCounts
                ) // 11–19 всегда "треков"
                lastOne == 1 -> context.getString(R.string.playlist_track_pluralize_1, tracksCounts)
                lastOne in 2..4 -> context.getString(
                    R.string.playlist_track_pluralize_2_4,
                    tracksCounts
                )
                else -> context.getString(R.string.playlist_track_pluralize_last, tracksCounts)
            }
        }
    }
}
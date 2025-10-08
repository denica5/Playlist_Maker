package com.denica.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.denica.playlistmaker.R
import com.denica.playlistmaker.databinding.TrackItemBinding
import com.denica.playlistmaker.search.domain.models.Song
import java.text.SimpleDateFormat
import java.util.Locale

class TrackListAdapter(
    val onItemClickListener: (Song) -> Unit,
    val onItemLongClickListener: (Song) -> Unit = {}
) :
    RecyclerView.Adapter<TrackListViewHolder>() {
    var itemList: List<Song> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        return TrackListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            onItemClickListener(itemList[holder.bindingAdapterPosition])
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(itemList[holder.bindingAdapterPosition])
            true
        }
    }

    companion object {

    }


}

class TrackListViewHolder(val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {


    fun bind(songDto: Song) {
        Glide.with(itemView.context).load(songDto.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder).centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .skipMemoryCache(true)
            .into(binding.trackImage)
        binding.trackName.text = songDto.trackName.trim()
        binding.trackArtistName.text = songDto.artistName.trim()
        binding.remainingTrackDurationMediaPlayer.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(songDto.trackTimeMillis).trim()
    }


    companion object {
        fun dpToPx(dp: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            ).toInt()
        }
    }
}

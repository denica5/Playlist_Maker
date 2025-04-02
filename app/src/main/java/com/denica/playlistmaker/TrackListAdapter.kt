package com.denica.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackListAdapter(private val itemList: List<Track>) :
    RecyclerView.Adapter<TrackListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: TrackListViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackImage: ImageView
    private val trackName: TextView
    private val trackArtistName: TextView
    private val trackDuration: TextView

    init {
        trackImage = itemView.findViewById(R.id.track_image)
        trackName = itemView.findViewById(R.id.track_name)
        trackArtistName = itemView.findViewById(R.id.track_artist_name)
        trackDuration = itemView.findViewById(R.id.track_duration)
    }

    fun bind(track: Track) {
        Glide.with(itemView.context).load(track.artworkUrl100).centerCrop()
            .transform(RoundedCorners(2)).into(trackImage)
        trackName.text = track.trackName
        trackArtistName.text = track.artistName
        trackDuration.text = track.trackTime
    }


}
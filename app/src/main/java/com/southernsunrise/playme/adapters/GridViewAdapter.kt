package com.southernsunrise.playme.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.southernsunrise.playme.R
import com.southernsunrise.playme.dataObjectModels.genre.Genre
import com.southernsunrise.playme.dataObjectModels.playlist.Playlist
import com.southernsunrise.playme.retrofit.utils.ApiConstants
import com.southernsunrise.playme.utilities.Constants.GENRE
import com.southernsunrise.playme.utilities.Constants.PLAYLIST
import com.squareup.picasso.Picasso

class GridViewAdapter(val fragment: Fragment, val itemList: ArrayList<Any> = ArrayList()) :
    BaseAdapter() {

    override fun getItem(p0: Int): Any {
        return itemList[p0]
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val itemView = getView(itemList[p0])!!
        setupItemView(itemList[p0], itemView, getItemType(itemList[p0])!!)

        return itemView
    }

    private fun setupItemView(listObj: Any, itemView: View, itemType: String) {
        when (itemType) {
            PLAYLIST -> setupPlaylistItemView(listObj, itemView)
            GENRE -> setupGenreItemView(listObj, itemView)
        }
    }

    private fun setupPlaylistItemView(listObj: Any, itemView: View) {
        listObj as Playlist
        val playlistPhotoImageView: ImageView =
            itemView.findViewById(R.id.iv_album_playlist_photo)
        val playlistNameTextView: TextView = itemView.findViewById(R.id.tv_album_playlist_name)
        val playlistArtistsNameTextView: TextView =
            itemView.findViewById(R.id.tv_album_date_playlist_artists_name)
        playlistArtistsNameTextView.isGone = true

        loadListItemPhoto(listObj, playlistPhotoImageView)
        playlistNameTextView.text = listObj.name
    }

    private fun setupGenreItemView(listObj: Any, itemView: View) {

    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    private fun getItemType(listObj: Any): String? {
        return when (listObj) {
            is Playlist -> PLAYLIST
            is Genre -> GENRE
            else -> null
        }
    }

    private fun getView(listObj: Any): View? {
        return when (getItemType(listObj)) {
            PLAYLIST -> {
                LayoutInflater.from(fragment.requireContext())!!
                    .inflate(R.layout.layout_album_playlist_grid_listitem, null)
            }
            GENRE -> {
                LayoutInflater.from(fragment.requireContext())!!
                    .inflate(R.layout.genres_list_listitem, null)

            }
            else -> null

        }

    }

    fun loadListItemPhoto(listObj: Any, imageView: ImageView) {
        var defaultImageResource = if (getItemType(listObj) == PLAYLIST) {
            R.drawable.img_default_track_listitem
        } else R.drawable.img_default_album_listitem

        val imgUrl = if (getItemType(listObj) == PLAYLIST) {
            ApiConstants.getImageLink(ApiConstants.PLAYLISTS, (listObj as Playlist).id, "300x300")
        } else ApiConstants.getImageLink(ApiConstants.GENRES, (listObj as Genre).id, "300x300")

        Picasso.get().load(imgUrl).placeholder(defaultImageResource).error(defaultImageResource)
            .into(imageView)

    }

}
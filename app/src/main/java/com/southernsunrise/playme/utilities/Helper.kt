package com.southernsunrise.playme.utilities

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.southernsunrise.playme.R
import com.southernsunrise.playme.activities.MainActivity
import com.southernsunrise.playme.dataObjectModels.album.Album
import com.southernsunrise.playme.dataObjectModels.track.Track
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

object Helper {

    var palette: Palette? = null

    val TAG = "helper"

    fun getGradientDrawable(palette: Palette, viewID: Int): GradientDrawable {
        val defaultValue: Int = Color.parseColor("#233033")
        val gradientDrawable: GradientDrawable = GradientDrawable()
        gradientDrawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM


        var startColor: Int = palette.getLightMutedColor(defaultValue)
        var endColor: Int = Color.parseColor("#FF1D1D1D")

        val ctlAlbumStartColor = palette.getMutedColor(defaultValue)
        val ctlAlbumEndColor = ctlAlbumStartColor.darken(60)

        val ctlArtistStartColor = palette.getMutedColor(defaultValue).darken(40)


        when (viewID) {


            R.id.cl_artist_details_play_btn -> {
                startColor = ctlArtistStartColor.darken(5)
                // ColorUtils.setAlphaComponent(startColor, 180)

                endColor = Color.TRANSPARENT
            }

            R.id.ctl_artist -> {
                startColor = ctlArtistStartColor
                endColor = startColor

                // endColor = palette.getDarkMutedColor(defaultValue)
            }

            R.id.ib_artist_play -> {

                startColor = palette.getMutedColor(defaultValue)
                val startColorLuminance = ColorUtils.calculateLuminance(startColor)

                if (startColorLuminance < 0.2) {
                    startColor = ColorUtils.blendARGB(startColor, Color.WHITE, 0.6f)
                } else if (startColorLuminance > 0.7) {
                    startColor = startColor.darken(((1 - startColorLuminance) * 100).toInt())

                }


                endColor = palette.getMutedColor(defaultValue).darken(40)

                gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
                gradientDrawable.gradientRadius = 90f

            }


            R.id.ib_album_play -> {

                startColor = palette.getMutedColor(defaultValue)
                val startColorLuminance = ColorUtils.calculateLuminance(startColor)

                if (startColorLuminance < 0.2) {
                    startColor = startColor.lighten(((1 - startColorLuminance) * 100).toInt())
                } else if (startColorLuminance > 0.7) {
                    startColor = startColor.darken(((1 - startColorLuminance) * 100).toInt())

                }

                endColor = startColor.darken(45)

                gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
                gradientDrawable.gradientRadius = 90f


            }

            R.id.ctl_album -> {

                startColor = ctlAlbumStartColor
                endColor = ctlAlbumEndColor

            }

            R.id.cl_album_details_play_btn -> {
                startColor = ctlAlbumEndColor
                endColor = Color.TRANSPARENT

            }

            R.id.ctl_playlist -> {
                startColor = ctlAlbumStartColor
                endColor = ctlAlbumEndColor


            }
            R.id.cl_playlist_details -> {
                startColor = ctlAlbumEndColor
                endColor = Color.parseColor("#121212")
            }

            R.id.ib_playlist_play -> {
                startColor = palette.getMutedColor(defaultValue)
                val startColorLuminance = ColorUtils.calculateLuminance(startColor)

                if (startColorLuminance < 0.2) {
                    startColor = startColor.lighten(((1 - startColorLuminance) * 100).toInt())
                } else if (startColorLuminance > 0.7) {
                    startColor = startColor.darken(((1 - startColorLuminance) * 100).toInt())

                }

                endColor = startColor.darken(45)

                gradientDrawable.gradientType = GradientDrawable.RADIAL_GRADIENT
                gradientDrawable.gradientRadius = 90f

            }

            R.id.player_root -> {
                startColor = palette.getMutedColor(defaultValue)
                endColor = Color.TRANSPARENT
            }


        }


        gradientDrawable.colors = intArrayOf(startColor, endColor)

        return gradientDrawable
    }


    private fun Int.darken(percent: Int): Int {
        // darkening given color to a certain percent
        val darkenedColorHSVArray = FloatArray(3)
        Color.colorToHSV(this, darkenedColorHSVArray)
        darkenedColorHSVArray[2] = darkenedColorHSVArray[2] * (100 - percent) / 100
        return Color.HSVToColor(darkenedColorHSVArray)

    }

    private fun Int.lighten(percent: Int): Int {
        // lightening given color to a certain percent
        val darkenedColorHSVArray = FloatArray(3)
        Color.colorToHSV(this, darkenedColorHSVArray)
        darkenedColorHSVArray[2] =
            darkenedColorHSVArray[2] + (darkenedColorHSVArray[2] * percent / 100)
        return Color.HSVToColor(darkenedColorHSVArray)

    }


    fun loadImageIntoImageViewAndSetupViewBackgroundsWithPalette(
        viewsList: ArrayList<View>,
        imageUrl: String
    ) {

        val imageView = viewsList[0] as ImageView
        val ctlDefaultScrimColor = R.color.color_grey_default_ctl

        setViewBackgrounds(viewsList, ctlDefaultScrimColor)

        imageView.apply {
            Picasso.get().load(imageUrl)
                .placeholder(this.getDefaultImageResource()).error(this.getDefaultImageResource())
                .into(this, object : Callback {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onSuccess() {
                        palette = drawable.toPalette()
                        setViewBackgrounds(viewsList, null)
                    }

                    override fun onError(e: java.lang.Exception?) {
                    }

                })
        }


    }

    fun loadPhotoIntoImageView(imageView: ImageView, imageUrl: String) {
        Picasso.get().load(imageUrl)
            .placeholder(imageView.getDefaultImageResource())
            .error(imageView.getDefaultImageResource())
            .into(imageView)
    }

    fun setViewBackgrounds(viewsList: ArrayList<View>, color: Int?) {
        for (view in viewsList) {
            if (color == null) {
                palette?.let {
                    view.setBackgroundDrawable(getGradientDrawable(it, view.id))
                }
            }
            color?.let {
                if (view.id == R.id.ctl_album || view.id == R.id.ctl_artist || view.id == R.id.ctl_playlist) {
                    view.apply {
                        setBackgroundResource(color)
                        background.alpha = 0
                    }
                }

            }
        }
    }

    fun Drawable.toPalette(): Palette {
        return Palette.from(this.toBitmap()).generate()
    }

    private fun ImageView.getDefaultImageResource(): Int {

        var defaultImageResource = 0
        when (this.id) {
            R.id.iv_album_cover -> {
                defaultImageResource = R.drawable.img_default_album_listitem
            }
            R.id.iv_artist_photo, R.id.iv_album_artist -> {
                defaultImageResource = R.drawable.img_deafault_artist_listitem
            }
            R.id.iv_playlist_photo, R.id.iv_track_image -> {
                defaultImageResource = R.drawable.img_default_track_listitem
            }

        }

        return defaultImageResource
    }


    fun getArtistAndTrackNamesText(dataPair: Pair<Any, Any>): SpannableStringBuilder {

        val tracksList: List<Track> = dataPair.second as List<Track>
        val spannableStringBuilder = SpannableStringBuilder()

        for (track in tracksList)

            if (dataPair.first is Album && !(dataPair.first as Album).madeByVariousArtists()) {
                if (tracksList.indexOf(track) <= 7) {
                    spannableStringBuilder.apply {
                        if (tracksList.indexOf(track) != 0) {
                            color(Color.parseColor("#B5FFFFFF")) {
                                append(" ${track.name}  •  ")
                            }
                            if (tracksList.indexOf(track) == 7 && (dataPair.first as Album).trackCount > 7) {
                                bold { color(Color.WHITE) { append(" and more") } }
                            }

                        } else {
                            if (tracksList.size > 1)
                                append("${track.name}  •  ")
                            else append(track.name)

                        }
                    }
                }

            } else {

                if (tracksList.indexOf(track) < 5) {
                    spannableStringBuilder
                        .bold {
                            if (tracksList.indexOf(track) == 0) {
                                color(Color.WHITE) { append(track.artistName) }
                            } else {
                                color(Color.parseColor("#B5FFFFFF")) { append("  •  ") }
                                color(Color.WHITE) { append(track.artistName) }
                            }

                        }
                        .color(Color.parseColor("#B5FFFFFF")) {
                            append(" ${track.name}")
                        }

                    if (tracksList.indexOf(track) == 4) {
                        spannableStringBuilder.bold {
                            color(Color.parseColor("#B5FFFFFF")) { append("  •  ") }
                            color(Color.WHITE) { append("and more") }
                        }
                    }
                }

            }
        return spannableStringBuilder
    }


    private fun Album.madeByVariousArtists(): Boolean {
        return this.contributingArtists?.primaryArtist == "art.0"
    }

    @SuppressLint("SimpleDateFormat")
    fun getAlbumFullDateText(album: Album): String {
        var fullReleaseDateString = ""

        album.let {
            val releaseDateString: String =
                it.originallyReleased
            val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val dateFormatter: SimpleDateFormat = SimpleDateFormat("MMM dd,  yyyy")
            val date = dateFormat.parse(releaseDateString)

            fullReleaseDateString = dateFormatter.format(date)
        }
        return fullReleaseDateString
    }


    fun setViewPaddings(fragment: Fragment, views: List<View>) {
        val displayDimen = (fragment.requireActivity() as MainActivity).getDisplayMatrix()
        val displayWidth = displayDimen["width"]
        val displayHeight = displayDimen["height"]

        for (view in views) {
            when (view) {
                is RecyclerView, is TextView -> {
                    displayWidth?.let {
                        view.setPadding(displayWidth.times(0.05).toInt(), 0, 0, 0)
                    }

                }
                is NestedScrollView -> {
                    displayHeight?.let {
                        view.setPadding(0, 0, 0, it.times(0.1).toInt())
                    }
                }
            }


        }


    }

    fun loadGenreImage(genreName: String, imageView: ImageView) {

        FirebaseFirestore.getInstance().collection("appData").document("music")
            .collection("genres")
            .document(genreName.replace("/", "_")).get().addOnSuccessListener {
                Picasso.get().load(it.getString("imageLink")).into(imageView)
            }.addOnFailureListener {
                Log.e(TAG, it.stackTraceToString())
            }


    }


    fun Bitmap.toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }


}


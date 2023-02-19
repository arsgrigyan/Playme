package com.southernsunrise.playme.activities

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.southernsunrise.playme.R
import com.southernsunrise.playme.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

    }

    fun getDisplayMatrix(): HashMap<String, Int> {
        val width: Int = this.resources.displayMetrics.widthPixels
        val height: Int = this.resources.displayMetrics.heightPixels
        return hashMapOf("width" to width, "height" to height)
    }

    fun showLoadingLayer(show: Boolean) {
        binding.loadingLayer.isVisible = show
    }


    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getStatusBarHeight(): Pair<Int?, Int> {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height = if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else Rect().apply { window.decorView.getWindowVisibleDisplayFrame(this) }.top
        val width = getDisplayMatrix()["width"]
        return Pair(width, height)
    }

}
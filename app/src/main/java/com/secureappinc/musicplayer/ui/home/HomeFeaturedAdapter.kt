package com.secureappinc.musicplayer.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */

internal class HomeFeaturedAdapter(var mContext: Context) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater
    var pages: List<YTTrendingItem> = ArrayList()

    init {
        mLayoutInflater = LayoutInflater.from(mContext)
    }

    // Returns the number of pages to be displayed in the ViewPager.
    override fun getCount(): Int {
        return pages.size
    }

    // Returns true if a particular object (page) is from a particular page
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    // This method should create the page for the given position passed to it as an argument.
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Inflate the layout for the page
        val itemView = mLayoutInflater.inflate(R.layout.item_featured, container, false)
        // Find and populate data into the page (i.e set the image)
        val imageView = itemView.findViewById(R.id.imgSong) as ImageView
        Picasso.get().load("https://img.youtube.com/vi/${pages[position].id}/maxresdefault.jpg")
                .fit()
                .into(imageView)
        // ...
        // Add the page to the container
        container.addView(itemView)
        // Return the page
        return itemView
    }

    // Removes the page from the container for the given position.
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
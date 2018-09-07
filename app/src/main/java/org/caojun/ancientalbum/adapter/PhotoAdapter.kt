package org.caojun.ancientalbum.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.caojun.ancientalbum.bean.Photo
import org.caojun.waterfall.internal.PLA_AbsListView

class PhotoAdapter(private val context: Context, private val list: List<Photo>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = PLA_AbsListView.LayoutParams(PLA_AbsListView.LayoutParams.WRAP_CONTENT, PLA_AbsListView.LayoutParams.WRAP_CONTENT)
            imageView.adjustViewBounds = true
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        Glide.with(context).load(list[position].originalUri).into(imageView)

        return imageView
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}
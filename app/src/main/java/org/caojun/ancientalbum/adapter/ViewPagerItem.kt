package org.caojun.ancientalbum.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.bean.Photo

class ViewPagerItem(private val context: Context): AdapterItem<Photo> {

    private lateinit var imageView: ImageView
    private lateinit var tvTopLeft: TextView
    private lateinit var tvTopRight: TextView
    private lateinit var tvBottomLeft: TextView
    private lateinit var tvBottomRight: TextView

    override fun getLayoutResId(): Int {
        return R.layout.adapter_viewpager
    }

    override fun bindViews(root: View) {
        imageView = root.findViewById(R.id.imageView)
        tvTopLeft = root.findViewById(R.id.tvTopLeft)
        tvTopRight = root.findViewById(R.id.tvTopRight)
        tvBottomLeft = root.findViewById(R.id.tvBottomLeft)
        tvBottomRight = root.findViewById(R.id.tvBottomRight)
    }

    override fun setViews() {
    }

    override fun handleData(t: Photo, position: Int) {
        Glide.with(context).load(t.originalUri).into(imageView)
    }
}
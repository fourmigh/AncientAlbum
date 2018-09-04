package org.caojun.ancientalbum.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.bean.Photo

class PhotoItem(private val context: Context): AdapterItem<Photo> {

    private lateinit var imageView: ImageView

    override fun getLayoutResId(): Int {
        return R.layout.adapter_photo
    }

    override fun bindViews(root: View) {
        imageView = root as ImageView
    }

    override fun setViews() {
    }

    override fun handleData(t: Photo, position: Int) {
        Glide.with(context).load(t.originalUri).into(imageView)
    }
}
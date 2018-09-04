package org.caojun.ancientalbum.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.bean.Folder

class FolderItem(private val context: Context): AdapterItem<Folder> {

    private lateinit var ivIcon: ImageView
    private lateinit var tvLabel: TextView

    override fun getLayoutResId(): Int {
        return R.layout.adapter_folder
    }

    override fun bindViews(root: View) {
        ivIcon = root.findViewById(R.id.ivIcon)
        tvLabel = root.findViewById(R.id.tvLabel)
    }

    override fun setViews() {
    }

    override fun handleData(t: Folder, position: Int) {
        Glide.with(context).load(t.iconUri).into(ivIcon)
        tvLabel.text = "${t.name}(${t.size})"
    }
}
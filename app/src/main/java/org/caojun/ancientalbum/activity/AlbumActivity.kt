package org.caojun.ancientalbum.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.layout_titlebar.*
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.ancientalbum.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * 照片缩略图列表
 */
class AlbumActivity: BaseAppCompatActivity() {

    companion object {
        const val Folder_Name = "Folder_Name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        toolbar.title = intent.getStringExtra(Folder_Name)
    }

    private fun readAlbum() {
        doAsync {


            uiThread {

            }
        }
    }
}
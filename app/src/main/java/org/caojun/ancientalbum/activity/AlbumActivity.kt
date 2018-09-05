package org.caojun.ancientalbum.activity

import android.app.ActivityOptions
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_album.*
import kotlinx.android.synthetic.main.layout_titlebar.*
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.adapter.PhotoItem
import org.caojun.ancientalbum.bean.Photo
import org.caojun.ancientalbum.utils.LocalImageHelper
import org.caojun.utils.FileUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.uiThread

/**
 * 照片缩略图列表
 */
class AlbumActivity: BaseAppCompatActivity() {

    companion object {
        const val Folder_Name = "Folder_Name"
        const val RequestCode_ViewPage = 1
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

        readAlbum()
    }

    private fun readAlbum() {
        doAsync {

            val photos = LocalImageHelper.instance.getFolder(toolbar.title.toString())

            uiThread {
                gridView.adapter = object : CommonAdapter<Photo>(photos, 1) {
                    override fun createItem(type: Any?): AdapterItem<*> {
                        return PhotoItem(this@AlbumActivity)
                    }
                }

                gridView.setOnItemClickListener { parent, view, position, id ->

                    val exif: ExifInterface
                    var date = ""
                    try {
                        exif = ExifInterface(FileUtils.getPath(Uri.parse(photos[position].originalUri), contentResolver))
                        date = exif.getAttribute(ExifInterface.TAG_DATETIME)
                    } catch (e: Exception) {
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val intent = Intent(this@AlbumActivity, PhotoActivity::class.java)
                        intent.putExtra(PhotoActivity.Folder_Name, toolbar.title)
                        intent.putExtra(PhotoActivity.Position, position)
                        intent.putExtra(PhotoActivity.Date, date)
                        val option = ActivityOptions.makeSceneTransitionAnimation(this@AlbumActivity, view, "transition")
                        startActivityForResult(intent, RequestCode_ViewPage, option.toBundle())
                    } else {
                        startActivityForResult<PhotoActivity>(RequestCode_ViewPage,
                                PhotoActivity.Folder_Name to toolbar.title,
                                PhotoActivity.Position to position,
                                PhotoActivity.Date to date)
                    }
                }
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == RequestCode_ViewPage && data != null) {
//            val position = data.getIntExtra(PhotoActivity.Position, 0)
//            gridView.smoothScrollToPosition(position)
//            return
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
}
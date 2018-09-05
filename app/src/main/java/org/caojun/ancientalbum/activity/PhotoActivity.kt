package org.caojun.ancientalbum.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.layout_titlebar.*
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.utils.LocalImageHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.net.Uri
import android.provider.MediaStore
import com.bumptech.glide.Glide

/**
 * 照片原图浏览
 */
class PhotoActivity: BaseAppCompatActivity() {

    companion object {
        const val Folder_Name = "Folder_Name"
        const val Position = "Position"
        const val Date = "Date"
        const val RequestCode_Share = 1
    }

    private lateinit var title: String
    private lateinit var uriShare: Uri
    private lateinit var date: String
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        title = intent.getStringExtra(Folder_Name)
        position = intent.getIntExtra(Position, 0)
        date = intent.getStringExtra(Date)

        readPhotos(position)
    }

    private fun readPhotos(position: Int) {
        doAsync {

            val photos = LocalImageHelper.instance.getFolder(title)

            uiThread {

                toolbar.title = getTitle(position)
                Glide.with(this@PhotoActivity).load(photos[position].originalUri).into(imageView)

                tvBottomRight.text = date
            }
        }
    }

    private fun getTitle(position: Int): String {
        val size = LocalImageHelper.instance.getFolder(title).size
        return "(${position + 1}/$size)$title"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.photo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_share -> {
                doShareBitmap()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doShareBitmap() {
        clearPhoto()

        root.isDrawingCacheEnabled = true
        root.buildDrawingCache()
        val bitmap = root.getDrawingCache(true)
        uriShare = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null))
        var intent = Intent()
        intent.action = Intent.ACTION_SEND//设置分享行为
        intent.type = "image/*"//设置分享内容的类型
        intent.putExtra(Intent.EXTRA_STREAM, uriShare)
        intent = Intent.createChooser(intent, getString(R.string.share))
        startActivityForResult(intent, RequestCode_Share)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearPhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode_Share) {
            clearPhoto()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun clearPhoto() {
        try {
            contentResolver.delete(uriShare, null, null)
        } catch (e: UninitializedPropertyAccessException) {
        } catch (e: Exception) {
        }
    }
}
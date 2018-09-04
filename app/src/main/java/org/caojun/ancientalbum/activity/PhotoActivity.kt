package org.caojun.ancientalbum.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_photo.*
import kotlinx.android.synthetic.main.layout_titlebar.*
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.CommonPagerAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.adapter.FolderItem
import org.caojun.ancientalbum.adapter.PhotoItem
import org.caojun.ancientalbum.adapter.ViewPagerItem
import org.caojun.ancientalbum.bean.Folder
import org.caojun.ancientalbum.bean.Photo
import org.caojun.ancientalbum.utils.LocalImageHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

/**
 * 照片原图浏览
 */
class PhotoActivity: BaseAppCompatActivity() {

    companion object {
        const val Folder_Name = "Folder_Name"
        const val Position = "Position"
    }

    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        title = intent.getStringExtra(Folder_Name)
        val position = intent.getIntExtra(Position, 0)

        readPhotos(position)
    }

    private fun readPhotos(position: Int) {
        doAsync {

            val photos = LocalImageHelper.instance.getFolder(title)

            uiThread {
                viewPager.adapter = object : CommonPagerAdapter<Photo>(photos) {
                    override fun createItem(type: Any?): AdapterItem<*> {
                        return ViewPagerItem(this@PhotoActivity)
                    }
                }

                viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    }

                    override fun onPageSelected(position: Int) {
                        toolbar.title = getTitle(position)
                    }
                })

                viewPager.setCurrentItem(position, false)
                toolbar.title = getTitle(position)
            }
        }
    }

    private fun getTitle(position: Int): String {
        val size = LocalImageHelper.instance.getFolder(title).size
        return "(${position + 1}/$size)$title"
    }

//    override fun onBackPressed() {
//        val intent = Intent()
//        intent.putExtra(Position, viewPager.currentItem)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
//    }
}
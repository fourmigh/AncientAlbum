package org.caojun.ancientalbum.activity

import android.Manifest
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_folders.*
import org.caojun.activity.BaseAppCompatActivity
import org.caojun.adapter.CommonAdapter
import org.caojun.adapter.bean.AdapterItem
import org.caojun.ancientalbum.R
import org.caojun.ancientalbum.adapter.FolderItem
import org.caojun.ancientalbum.bean.Folder
import org.caojun.ancientalbum.utils.LocalImageHelper
import org.caojun.utils.ActivityUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

/**
 * 文件夹列表
 */
class FoldersActivity: BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_folders)

        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, object : ActivityUtils.RequestPermissionListener {
            override fun onSuccess() {
                readFolders()
            }

            override fun onFail() {
            }
        })

        tvProgressBar.text = getString(R.string.initializing, getString(R.string.app_name))
    }

    private fun readFolders() {
        doAsync {
            LocalImageHelper.init(this@FoldersActivity)

            val folders = ArrayList<Folder>()

            for ((key, value) in LocalImageHelper.instance.folders) {
                val uri = value[0].thumbnailUri
                val size = value.size
                val folder = Folder(uri, key, size)
                folders.add(folder)
            }

            uiThread {

                listView.adapter = object : CommonAdapter<Folder>(folders, 1) {
                    override fun createItem(type: Any?): AdapterItem<*> {
                        return FolderItem(this@FoldersActivity)
                    }
                }

                listView.setOnItemClickListener { parent, view, position, id ->
                    startActivity<AlbumActivity>(AlbumActivity.Folder_Name to folders[position].name)
                }

                progressBar.visibility = View.GONE
                listView.visibility = View.VISIBLE
            }
        }
    }
}
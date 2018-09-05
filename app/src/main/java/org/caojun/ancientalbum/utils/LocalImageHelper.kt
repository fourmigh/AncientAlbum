package org.caojun.ancientalbum.utils

import android.content.Context
import android.media.ExifInterface
import android.provider.MediaStore
import android.text.TextUtils
import org.caojun.ancientalbum.bean.Photo
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import android.net.Uri
import org.caojun.utils.FileUtils
import java.io.IOException

class LocalImageHelper private constructor(private val context: Context) {

    private val paths: MutableList<Photo> = ArrayList()

    private val folders: MutableMap<String, MutableList<Photo>> = HashMap()

    val getFolders: Map<String, MutableList<Photo>>
        get() = folders

    private val isInited: Boolean
        get() = paths.size > 0

    private var isRunning = false

    @Synchronized
    private fun initImage() {
        if (isRunning)
            return
        isRunning = true
        if (isInited)
            return
        //获取大图的游标
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 大图URI
                STORE_IMAGES, null, null, // No where clause
                MediaStore.Images.Media.DATE_TAKEN + " DESC") ?: return// 字段
        // No where clause
        //根据时间升序
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)//大图ID
            val path = cursor.getString(1)//大图路径
            val file = File(path)
            //判断大图是否存在
            if (file.exists()) {
                //获取大图URI
                val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build().toString()
                if (TextUtils.isEmpty(uri)) {
                    continue
                }
                //获取目录名
                val folder = file.parentFile.name

//                val exif: ExifInterface
//                try {
//                    exif = ExifInterface(FileUtils.getPath(Uri.parse(uri), context.contentResolver))
//                } catch (e: IOException) {
//                    continue
//                }
                val photoFile = Photo(uri)

                paths.add(photoFile)
                //判断文件夹是否已经存在
                if (folders.containsKey(folder)) {
                    folders[folder]?.add(photoFile)
                } else {
                    val files = ArrayList<Photo>()
                    files.add(photoFile)
                    folders[folder] = files
                }
            }
        }
//        folders["所有图片"] = paths
        cursor.close()
        isRunning = false
    }

//    private fun getThumbnail(id: Int): String? {
//        //获取大图的缩略图
//        val cursor = context.contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//                THUMBNAIL_STORE_IMAGE,
//                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
//                arrayOf(id.toString() + ""), null)
//        if (cursor!!.count > 0) {
//            cursor.moveToFirst()
//            val thumId = cursor.getInt(0)
//            val uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(thumId)).build().toString()
//            cursor.close()
//            return uri
//        }
//        cursor.close()
//        return null
//    }

    fun getFolder(folder: String): MutableList<Photo> {
        return folders[folder]!!
    }

    companion object {
        lateinit var instance: LocalImageHelper
            private set

        //大图遍历字段
        private val STORE_IMAGES = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)

        fun init(context: Context) {
            instance = LocalImageHelper(context)
            instance.initImage()
        }
    }
}

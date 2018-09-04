package org.caojun.ancientalbum.utils

import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils
import org.caojun.ancientalbum.bean.Photo
import java.io.File
import java.util.ArrayList
import java.util.HashMap

class LocalImageHelper private constructor(private val context: Context) {
    val checkedItems: List<Photo> = ArrayList()

    //当前选中得图片个数
    var currentSize: Int = 0

    internal val paths: MutableList<Photo> = ArrayList()

    internal val folders: MutableMap<String, MutableList<Photo>> = HashMap()

    val folderMap: Map<String, MutableList<Photo>>
        get() = folders

    val isInited: Boolean
        get() = paths.size > 0

    var isResultOk: Boolean = false

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
                //小图URI
                var thumbUri = getThumbnail(id, path)
                //获取大图URI
                val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build().toString()
                if (TextUtils.isEmpty(uri))
                    continue
                if (TextUtils.isEmpty(thumbUri))
                    thumbUri = uri
                //获取目录名
                val folder = file.parentFile.name

//                var degree = cursor.getInt(2)
//                if (degree != 0) {
//                    degree += 180
//                }
//                localFile.orientation = 360 - degree
//                val photoFile = Photo(uri, thumbUri!!, 360 - degree)
                val photoFile = Photo(uri, thumbUri!!)

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

    private fun getThumbnail(id: Int, path: String): String? {
        //获取大图的缩略图
        val cursor = context.contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                THUMBNAIL_STORE_IMAGE,
                MediaStore.Images.Thumbnails.IMAGE_ID + " = ?",
                arrayOf(id.toString() + ""), null)
        if (cursor!!.count > 0) {
            cursor.moveToFirst()
            val thumId = cursor.getInt(0)
            val uri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI.buildUpon().appendPath(Integer.toString(thumId)).build().toString()
            cursor.close()
            return uri
        }
        cursor.close()
        return null
    }

    fun getFolder(folder: String): MutableList<Photo> {
        return folders[folder]!!
    }

    //    public void clear(){
    //        checkedItems.clear();
    //        currentSize=(0);
    //        String foloder= AppContext.getInstance().getCachePath()
    //                + "/PostPicture/";
    //        File savedir = new File(foloder);
    //        if (savedir.exists()) {
    //            deleteFile(savedir);
    //        }
    //    }
    //    public void deleteFile(File file) {
    //
    //        if (file.exists()) {
    //            if (file.isFile()) {
    //                file.delete();
    //            } else if (file.isDirectory()) {
    //                File files[] = file.listFiles();
    //                for (int i = 0; i < files.length; i++) {
    //                    deleteFile(files[i]);
    //                }
    //            }
    //        } else {
    //        }
    //    }
//    class LocalFile {
//        var originalUri: String? = null//原图URI
//        var thumbnailUri: String? = null//缩略图URI
//        var orientation: Int = 0//图片旋转角度
//
//    }

    companion object {
        lateinit var instance: LocalImageHelper
            private set
        //    public String getCameraImgPath() {
        //        return CameraImgPath;
        //    }

        //    public String setCameraImgPath() {
        //        String foloder= AppContext.getInstance().getCachePath()
        //                + "/PostPicture/";
        //        File savedir = new File(foloder);
        //        if (!savedir.exists()) {
        //            savedir.mkdirs();
        //        }
        //        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
        //                .format(new Date());
        //        // 照片命名
        //        String picName =  timeStamp + ".jpg";
        //        //  裁剪头像的绝对路径
        //        CameraImgPath = foloder + picName;
        //        return  CameraImgPath;
        //    }

        //拍照时指定保存图片的路径
        //    private String CameraImgPath;
        //大图遍历字段
        private val STORE_IMAGES = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION)
        //小图遍历字段
        private val THUMBNAIL_STORE_IMAGE = arrayOf(MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.DATA)

        fun init(context: Context) {
            instance = LocalImageHelper(context)
//            Thread(Runnable { instance.initImage() }).start()
            instance.initImage()
        }
    }
}

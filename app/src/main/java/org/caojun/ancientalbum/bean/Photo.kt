package org.caojun.ancientalbum.bean

import android.media.ExifInterface

data class Photo(val originalUri: String, val thumbnailUri: String, val exif: ExifInterface)
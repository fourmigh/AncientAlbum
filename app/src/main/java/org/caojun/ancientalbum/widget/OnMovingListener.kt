package org.caojun.ancientalbum.widget

/**
 * MatrixImageView移动监听接口, 用以组织ViewPager对Move操作的拦截
 */
interface OnMovingListener {

    fun startDrag()

    fun stopDrag()
}

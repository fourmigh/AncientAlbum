package org.caojun.ancientalbum.widget

import android.graphics.Matrix
import android.graphics.PointF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

class MatrixTouchListener(private val matrixImageView: MatrixImageView) : View.OnTouchListener {

    private var mGestureDetector: GestureDetector? = null
    private var moveListener: OnMovingListener? = null
    /**
     * 最大缩放级别
     */
    internal var mMaxScale = 6f
    /**
     * 双击时的缩放级别
     */
    internal var mDobleClickScale = 2f
    private var mMode = 0//
    /**
     * 缩放开始时的手指间距
     */
    private var mStartDis: Float = 0.toFloat()
    /**
     * 当前Matrix
     */
    private val mCurrentMatrix = Matrix()

    /** 用于记录开始时候的坐标位置  */

    /**
     * 和ViewPager交互相关，判断当前是否可以左移、右移
     */
    internal var mLeftDragable: Boolean = false
    internal var mRightDragable: Boolean = false
    /**
     * 是否第一次移动
     */
    internal var mFirstMove = false
    private val mStartPoint = PointF()

    /**
     * 判断缩放级别是否是改变过
     *
     * @return true表示非初始值, false表示初始值
     */
    private//获取当前X轴缩放级别
    //获取模板的X轴缩放级别，两者做比较
    val isZoomChanged: Boolean
        get() {
            val values = FloatArray(9)
            matrixImageView.imageMatrix.getValues(values)
            val scale = values[Matrix.MSCALE_X]
            return scale != matrixImageView.mScale
        }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //设置拖动模式
                mMode = MODE_DRAG
                mStartPoint.set(event.x, event.y)
                isMatrixEnable()
                startDrag()
                checkDragable()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                reSetMatrix()
                stopDrag()
            }
            MotionEvent.ACTION_MOVE -> if (mMode == MODE_ZOOM) {
                setZoomMatrix(event)
            } else if (mMode == MODE_DRAG) {
                setDragMatrix(event)
            } else {
                stopDrag()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (mMode == MODE_UNABLE) return true
                mMode = MODE_ZOOM
                mStartDis = distance(event)
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
            else -> {
            }
        }
        return mGestureDetector!!.onTouchEvent(event)
    }

    /**
     * 子控件开始进入移动状态，令ViewPager无法拦截对子控件的Touch事件
     */
    private fun startDrag() {
        if (moveListener != null) moveListener!!.startDrag()

    }

    /**
     * 子控件开始停止移动状态，ViewPager将拦截对子控件的Touch事件
     */
    private fun stopDrag() {
        if (moveListener != null) moveListener!!.stopDrag()
    }

    /**
     * 根据当前图片左右边缘设置可拖拽状态
     */
    private fun checkDragable() {
        mLeftDragable = true
        mRightDragable = true
        mFirstMove = true
        val values = FloatArray(9)
        matrixImageView.imageMatrix.getValues(values)
        //图片左边缘离开左边界，表示不可右移
        if (values[Matrix.MTRANS_X] >= 0)
            mRightDragable = false
        //图片右边缘离开右边界，表示不可左移
        if (matrixImageView.mImageWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X] <= matrixImageView.width) {
            mLeftDragable = false
        }
    }

    /**
     * 设置拖拽状态下的Matrix
     *
     * @param event
     */
    fun setDragMatrix(event: MotionEvent) {
        if (isZoomChanged) {
            var dx = event.x - mStartPoint.x // 得到x轴的移动距离
            var dy = event.y - mStartPoint.y // 得到x轴的移动距离
            //避免和双击冲突,大于10f才算是拖动
            if (Math.sqrt((dx * dx + dy * dy).toDouble()) > 10f) {
                mStartPoint.set(event.x, event.y)
                //在当前基础上移动
                mCurrentMatrix.set(matrixImageView.imageMatrix)
                val values = FloatArray(9)
                mCurrentMatrix.getValues(values)
                dy = checkDyBound(values, dy)
                dx = checkDxBound(values, dx, dy)

                mCurrentMatrix.postTranslate(dx, dy)
                matrixImageView.imageMatrix = mCurrentMatrix
            }
        } else {
            stopDrag()
        }
    }

    /**
     * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dy
     * @return
     */
    private fun checkDyBound(values: FloatArray, dy: Float): Float {
        var y = dy
        val height = matrixImageView.height.toFloat()
        if (matrixImageView.mImageHeight * values[Matrix.MSCALE_Y] < height)
            return 0f
        if (values[Matrix.MTRANS_Y] + dy > 0)
            y = -values[Matrix.MTRANS_Y]
        else if (values[Matrix.MTRANS_Y] + dy < -(matrixImageView.mImageHeight * values[Matrix.MSCALE_Y] - height))
            y = -(matrixImageView.mImageHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y]
        return y
    }

    /**
     * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dx
     * @return
     */
    private fun checkDxBound(values: FloatArray, dx: Float, dy: Float): Float {
        var x = dx
        val width = matrixImageView.width.toFloat()
        if (!mLeftDragable && dx < 0) {
            //加入和y轴的对比，表示在监听到垂直方向的手势时不切换Item
            if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
                stopDrag()
            }
            return 0f
        }
        if (!mRightDragable && dx > 0) {
            //加入和y轴的对比，表示在监听到垂直方向的手势时不切换Item
            if (Math.abs(dx) * 0.4f > Math.abs(dy) && mFirstMove) {
                stopDrag()
            }
            return 0f
        }
        mLeftDragable = true
        mRightDragable = true
        if (mFirstMove) mFirstMove = false
        if (matrixImageView.mImageWidth * values[Matrix.MSCALE_X] < width) {
            return 0f

        }
        if (values[Matrix.MTRANS_X] + dx > 0) {
            x = -values[Matrix.MTRANS_X]
        } else if (values[Matrix.MTRANS_X] + dx < -(matrixImageView.mImageWidth * values[Matrix.MSCALE_X] - width)) {
            x = -(matrixImageView.mImageWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X]
        }
        return x
    }

    /**
     * 设置缩放Matrix
     *
     * @param event
     */
    private fun setZoomMatrix(event: MotionEvent) {
        //只有同时触屏两个点的时候才执行
        if (event.pointerCount < 2) return
        val endDis = distance(event)// 结束距离
        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
            var scale = endDis / mStartDis// 得到缩放倍数
            mStartDis = endDis//重置距离
            mCurrentMatrix.set(matrixImageView.imageMatrix)//初始化Matrix
            val values = FloatArray(9)
            mCurrentMatrix.getValues(values)
            scale = checkMaxScale(scale, values)
            val centerF = getCenter(scale, values)
            mCurrentMatrix.postScale(scale, scale, centerF.x, centerF.y)
            matrixImageView.imageMatrix = mCurrentMatrix
        }
    }

    /**
     * 获取缩放的中心点。
     *
     * @param scale
     * @param values
     * @return
     */
    private fun getCenter(scale: Float, values: FloatArray): PointF {
        //缩放级别小于原始缩放级别时或者为放大状态时，返回ImageView中心点作为缩放中心点
        if (scale * values[Matrix.MSCALE_X] < matrixImageView.mScale || scale >= 1) {
            return PointF((matrixImageView.width / 2).toFloat(), (matrixImageView.height / 2).toFloat())
        }
        var cx = (matrixImageView.width / 2).toFloat()
        val cy = (matrixImageView.height / 2).toFloat()
        //以ImageView中心点为缩放中心，判断缩放后的图片左边缘是否会离开ImageView左边缘，是的话以左边缘为X轴中心
        if ((matrixImageView.width / 2 - values[Matrix.MTRANS_X]) * scale < matrixImageView.width / 2)
            cx = 0f
        //判断缩放后的右边缘是否会离开ImageView右边缘，是的话以右边缘为X轴中心
        if ((matrixImageView.mImageWidth * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X]) * scale < matrixImageView.width)
            cx = matrixImageView.width.toFloat()
        return PointF(cx, cy)
    }

    /**
     * 检验scale，使图像缩放后不会超出最大倍数
     *
     * @param scale
     * @param values
     * @return
     */
    private fun checkMaxScale(scale: Float, values: FloatArray): Float {
        var s = scale
        if (scale * values[Matrix.MSCALE_X] > mMaxScale)
            s = mMaxScale / values[Matrix.MSCALE_X]
        return s
    }

    /**
     * 重置Matrix
     */
    private fun reSetMatrix() {
        if (checkRest()) {
            mCurrentMatrix.set(matrixImageView.mMatrix)
            matrixImageView.imageMatrix = mCurrentMatrix
        } else {
            //判断Y轴是否需要更正
            val values = FloatArray(9)
            matrixImageView.imageMatrix.getValues(values)
            val height = matrixImageView.mImageHeight * values[Matrix.MSCALE_Y]
            if (height < matrixImageView.height) {
                //在图片真实高度小于容器高度时，Y轴居中，Y轴理想偏移量为两者高度差/2，
                val topMargin = (matrixImageView.height - height) / 2
                if (topMargin != values[Matrix.MTRANS_Y]) {
                    mCurrentMatrix.set(matrixImageView.imageMatrix)
                    mCurrentMatrix.postTranslate(0f, topMargin - values[Matrix.MTRANS_Y])
                    matrixImageView.imageMatrix = mCurrentMatrix
                }
            }
        }
    }

    /**
     * 判断是否需要重置
     *
     * @return 当前缩放级别小于模板缩放级别时，重置
     */
    private fun checkRest(): Boolean {
        // TODO Auto-generated method stub
        val values = FloatArray(9)
        matrixImageView.imageMatrix.getValues(values)
        //获取当前X轴缩放级别
        val scale = values[Matrix.MSCALE_X]
        //获取模板的X轴缩放级别，两者做比较
        return scale < matrixImageView.mScale
    }

    /**
     * 判断是否支持Matrix
     */
    private fun isMatrixEnable() {
        //当加载出错时，不可缩放
        if (matrixImageView.scaleType != ImageView.ScaleType.CENTER) {
            matrixImageView.scaleType = ImageView.ScaleType.MATRIX
        } else {
            mMode = MODE_UNABLE//设置为不支持手势
        }
    }

    /**
     * 计算两个手指间的距离
     *
     * @param event
     * @return
     */
    private fun distance(event: MotionEvent): Float {
        val dx = event.getX(1) - event.getX(0)
        val dy = event.getY(1) - event.getY(0)
        /** 使用勾股定理返回两点之间的距离  */
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    /**
     * 双击时触发
     */
    fun onDoubleClick() {
        val scale = if (isZoomChanged) 1f else mDobleClickScale
        mCurrentMatrix.set(matrixImageView.mMatrix)//初始化Matrix
        mCurrentMatrix.postScale(scale, scale, (matrixImageView.width / 2).toFloat(), (matrixImageView.height / 2).toFloat())
        matrixImageView.imageMatrix = mCurrentMatrix
    }

    fun setGestureDetector(mGestureDetector: GestureDetector) {
        this.mGestureDetector = mGestureDetector
    }

    fun setOnMovingListener(listener: OnMovingListener) {
        moveListener = listener
    }

    companion object {

        /**
         * 拖拉照片模式
         */
        private val MODE_DRAG = 1
        /**
         * 放大缩小照片模式
         */
        private val MODE_ZOOM = 2
        /**
         * 不支持Matrix
         */
        private val MODE_UNABLE = 3
    }
}

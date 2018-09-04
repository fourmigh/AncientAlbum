package org.caojun.ancientalbum.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView

/**
 * @author LinJ
 * @ClassName: MatrixImageView
 * @Description: 带放大、缩小、移动效果的ImageView
 * @date 2015-1-7 上午11:15:07
 */
class MatrixImageView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {
    //    private GestureDetector mGestureDetector;
    /**
     * 模板Matrix，用以初始化
     */
    var mMatrix = Matrix()
    /**
     * 图片长度
     */
    var mImageWidth: Float = 0.toFloat()
    /**
     * 图片高度
     */
    var mImageHeight: Float = 0.toFloat()
    /**
     * 原始缩放级别
     */
    var mScale: Float = 0.toFloat()
    //    private OnMovingListener moveListener;
    private var singleTapListener: OnSingleTapListener? = null

    private val mMatrixTouchListener: MatrixTouchListener

    init {
        mMatrixTouchListener = MatrixTouchListener(this)
        setOnTouchListener(mMatrixTouchListener)
        //        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
        mMatrixTouchListener.setGestureDetector(GestureDetector(getContext(), GestureListener(mMatrixTouchListener)))
        //背景设置为balck
        setBackgroundColor(Color.BLACK)
        //将缩放类型设置为CENTER_INSIDE，表示把图片居中显示,并且宽高最大值为控件宽高
        scaleType = ImageView.ScaleType.FIT_CENTER
    }

    //    public MatrixImageView(Context context) {
    //        super(context, null);
    //        MatrixTouchListener mListener = new MatrixTouchListener();
    //        setOnTouchListener(mListener);
    //        mGestureDetector = new GestureDetector(getContext(), new GestureListener(mListener));
    //        //背景设置为balck
    //        setBackgroundColor(Color.BLACK);
    //        //将缩放类型设置为CENTER_INSIDE，表示把图片居中显示,并且宽高最大值为控件宽高
    //        setScaleType(ScaleType.FIT_CENTER);
    //    }

    fun setOnMovingListener(listener: OnMovingListener) {
        mMatrixTouchListener.setOnMovingListener(listener)
    }

    fun setOnSingleTapListener(onSingleTapListener: OnSingleTapListener) {
        this.singleTapListener = onSingleTapListener
    }

    override fun setImageBitmap(bm: Bitmap) {
        // TODO Auto-generated method stub
        super.setImageBitmap(bm)
        //大小为0 表示当前控件大小未测量  设置监听函数  在绘制前赋值
        if (width == 0) {
            val vto = viewTreeObserver
            vto.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    initData()
                    //赋值结束后，移除该监听函数
                    this@MatrixImageView.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            })
        } else {
            initData()
        }
    }

    /**
     * 初始化模板Matrix和图片的其他数据
     */
    private fun initData() {
        //设置完图片后，获取该图片的坐标变换矩阵
        mMatrix.set(imageMatrix)
        val values = FloatArray(9)
        mMatrix.getValues(values)
        //图片宽度为屏幕宽度除缩放倍数
        mImageWidth = width / values[Matrix.MSCALE_X]
        mImageHeight = (height - values[Matrix.MTRANS_Y] * 2) / values[Matrix.MSCALE_Y]
        mScale = values[Matrix.MSCALE_X]
    }

    //    public class MatrixTouchListener implements OnTouchListener {
    //    }


    private inner class GestureListener(private val listener: MatrixTouchListener) : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            //捕获Down事件
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            //触发双击事件
            listener.onDoubleClick()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            // TODO Auto-generated method stub
            return super.onSingleTapUp(e)
        }

        override fun onLongPress(e: MotionEvent) {
            // TODO Auto-generated method stub
            super.onLongPress(e)
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                             velocityY: Float): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onShowPress(e: MotionEvent) {
            // TODO Auto-generated method stub
            super.onShowPress(e)
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            // TODO Auto-generated method stub
            return super.onDoubleTapEvent(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (singleTapListener != null) singleTapListener!!.onSingleTap()
            return super.onSingleTapConfirmed(e)
        }

    }

    companion object {
        val TAG = "MatrixImageView"
    }

    //    /**
    //     * @author LinJ
    //     * @ClassName: OnChildMovingListener
    //     * @Description: MatrixImageView移动监听接口, 用以组织ViewPager对Move操作的拦截
    //     * @date 2015-1-12 下午4:39:32
    //     */
    //    public interface OnMovingListener {
    //        public void startDrag();
    //
    //        public void stopDrag();
    //    }

    //    /**
    //     * @author LinJ
    //     * @ClassName: OnSingleTapListener
    //     * @Description: 监听ViewPager屏幕单击事件，本质是监听子控件MatrixImageView的单击事件
    //     * @date 2015-1-12 下午4:48:52
    //     */
    //    public interface OnSingleTapListener {
    //        public void onSingleTap();
    //    }
}
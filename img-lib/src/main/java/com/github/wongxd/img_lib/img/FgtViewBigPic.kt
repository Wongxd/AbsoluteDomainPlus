package com.github.wongxd.img_lib.img

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.wongxd.core_lib.base.utils.utilcode.util.BarUtils
import com.github.wongxd.core_lib.custom.CustPagerTransformer
import com.github.wongxd.core_lib.custom.pinchImageview.PinchImageView
import com.github.wongxd.core_lib.data.bean.Task
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.data.download.DownLoadManager
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.core_lib.util.Tips
import com.github.wongxd.img_lib.R
import loadOriginScaleImg

class FgtViewBigPic : BaseBackFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fgt_view_big_pic
    }

    private var adapter: PagerAdapter? = null
    private lateinit var mViewPager: ViewPager
    private var mList: MutableList<String> = ArrayList()


    private lateinit var fgt: FgtViewBigPic
    private var currentPos = 0
    private var total = 0

    private var tvTip: TextView? = null
    private lateinit var tvSave: TextView


    private lateinit var mVm: SeePicViewModel

    private fun initVp(v: View) {

        val tvReturn: TextView = v.findViewById(R.id.tv_return_fgt_drag_photo)
        tvReturn.setOnClickListener { pop() }

        tvTip = v.findViewById(R.id.tv_fgt_drag_photo)
        tvSave = v.findViewById(R.id.tv_save_big_image_fgt_drag_photo)

        mViewPager = v.findViewById<View>(R.id.vp_fgt_drag_photo) as ViewPager

        mViewPager.removeAllViews()
        mViewPager.setPageTransformer(false, CustPagerTransformer(activity))

        adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return mList.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {

                val mPhotoView = View.inflate(activity, R.layout.item_vp_view_big_pic, null) as PinchImageView

                mPhotoView.loadOriginScaleImg(mList[position])


                container.addView(mPhotoView)
                return mPhotoView

            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as PinchImageView)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view === `object`
            }
        }

        mViewPager.adapter = adapter

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                currentPos = position
                tvSave.setOnClickListener {
                    DownLoadManager.get().addTask(Task(url = mList[currentPos], type = TaskType.IMG))
                    Tips.showSuccessTips(activity as AppCompatActivity, "提示", "已经添加到下载列表")
                }
                tvTip?.text = (currentPos + 1).toString() + "/" + total


            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })



        tvSave.setOnClickListener {
            DownLoadManager.get().addTask(Task(url = mList[currentPos], type = TaskType.IMG))
            Tips.showSuccessTips(activity as AppCompatActivity, "提示", "已经添加到下载列表")
        }


    }


    override fun onDestroyView() {
        mVm.currentImgPos.value = currentPos
        super.onDestroyView()
        if (!BarUtils.isStatusBarVisible(activity))
            BarUtils.setStatusBarVisibility(activity, true)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initVp(mRootView)
        fgt = this
        mVm = ViewModelProviders.of(activity ?: _mActivity).get(SeePicViewModel::class.java)
        mVm.picTotalList.observe(this, Observer<MutableList<String>> { t ->
            if (t != null) {
                mList.clear()
                mList.addAll(t)
                adapter?.notifyDataSetChanged()
                total = mList.size

                if (currentPos == 0) {
                    try {
                        currentPos = mVm.currentImgPos.value!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                        currentPos = 0
                    }

                    mViewPager.currentItem = currentPos
                }
                tvTip?.text = (currentPos + 1).toString() + "/" + total
            }
        })


        if (BarUtils.isStatusBarVisible(activity))
            BarUtils.setStatusBarVisibility(activity, false)
    }


//    private lateinit var mRootView: View
//    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        mRootView = inflater?.inflate(R.layout.fgt_view_big_pic, container, false)!!
//        val mConfig = SliderConfig.Builder()
//                .position(SliderPosition.VERTICAL)
//                .velocityThreshold(2400f)
//                .distanceThreshold(.75f)
//                .sliderListener(listener)
//                .edge(false)
//                .build()
//
//        val iSlider = SliderUtils.attachV4Fragment(this, mRootView, mConfig)
//        return iSlider.sliderView
//    }

//    private val listener = object : SliderListenerAdapter() {
//
//        override fun onSlideOpened() {
//
//        }
//
//        override fun onSlideClosed() {
//            pop()
//        }
//
//        override fun onSlideChange(percent: Float) {
//    super.onSlideChange(percent)
//            mRootView.scaleX = percent
//            mRootView.scaleY = percent
//        }
//    }


    companion object {

        fun newInstance(b: Bundle): FgtViewBigPic {
            val fgt = FgtViewBigPic()
            fgt.arguments = b
            return fgt
        }
    }
}

package com.cyxbs.pages.course.widget.fragment.vp

import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.cyxbs.components.config.sp.defaultSp
import com.cyxbs.pages.course.widget.R
import com.cyxbs.pages.course.widget.fragment.vp.expose.IHeaderCourseVp
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.invisible
import com.mredrock.cyxbs.lib.utils.extensions.lazyUnlock
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import kotlin.math.max

/**
 * 在 [AbstractCourseVpFragment] 的基础上封装了课表头
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/2 13:18
 */
@Suppress("LeakingThis")
abstract class AbstractHeaderCourseVpFragment : AbstractCourseVpFragment(), IHeaderCourseVp {

  override val mHeader by R.id.course_header.view<ViewGroup>()

  override val mHeaderContent by R.id.course_header_content.view<ViewGroup>()

  override val mHeaderTab by R.id.course_header_tab.view<ViewGroup>()

  override val mViewPager by R.id.course_vp.view<ViewPager2>()

  override val mTvNowWeek by R.id.course_iv_this_week.view<TextView>()

  override val mTvWhichWeek by R.id.course_tv_which_week.view<TextView>()
    .addInitialize {
      setOnSingleClickListener {
        showTabLayout()
      }
    }

  override val mBtnBackNowWeek by R.id.course_btn_back_now_week.view<Button>()
    .addInitialize {
      setOnSingleClickListener {
        mViewPager.currentItem = getPositionByNowWeek()
      }
    }

  override val mTabLayout by R.id.course_tab_layout.view<TabLayout>()

  override val mIvTableBack by R.id.course_iv_tab_back.view<ImageView>()

  protected val mWhichWeekStr by lazyUnlock {
    resources.getStringArray(com.cyxbs.pages.course.api.R.array.course_api_course_weeks_strings)
  }

  /**
   * 如果你要想在 Header 上新增东西，请重写该方法，并且在 xml 中使用 <include> 来包含原有的 Header 控件
   * 注意控件 id 要一致
   */
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.course_widget_layout_vp_with_header, container, false)
  }

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initViewPager()
    initTabLayout()
  }

  private fun initViewPager() {
    mViewPager.registerOnPageChangeCallback(
      object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
          mTvWhichWeek.text = getWhichWeekStr(position)
        }

        override fun onPageScrolled(
          position: Int,
          positionOffset: Float,
          positionOffsetPixels: Int
        ) {
          showNowWeek(position, positionOffset)
        }
      }
    )
    if (!defaultSp.getBoolean("已显示课表tab引导", false)) {
      mViewPager.registerOnPageChangeCallback(
        object : ViewPager2.OnPageChangeCallback() {
          var lastTimestamp = 0L
          var count = 0
          override fun onPageSelected(position: Int) {
            val now = System.currentTimeMillis()
            if (now - lastTimestamp > 1000) {
              lastTimestamp = now
              count = 0
            } else count++
            if (count >= 2) {
              defaultSp.edit { putBoolean("已显示课表tab引导", true) }
              // 官方未做迭代中删除处理，只能 post 删除
              mViewPager.post { mViewPager.unregisterOnPageChangeCallback(this) }
              // 目前掌邮没有气泡引导功能，暂时使用 toast 提示
              toastLong("点击“第几周”可快速跳转周数")
            }
          }
        }
      )
    }
  }

  protected open fun initTabLayout() {
    TabLayoutMediator(mTabLayout, mViewPager) { tab, position ->
      tab.text = mWhichWeekStr[position]
    }.attach()
    mIvTableBack.setOnSingleClickListener {
      hideTabLayout()
    }
    // 先设置成 invisible，在布局后才设置成 gone
    // 如果一来就是 gone，则第一次显示时 tab 不会居中显示当前页
    mHeaderTab.invisible()
    mHeaderTab.post { mHeaderTab.gone() }
  }

  /**
   * 根据本周得到对应 Vp 中的 position
   */
  protected open fun getPositionByNowWeek(): Int {
    // 回到本周，如果周数大于等于了总的 itemCount，则默认显示第 0 页
    return if (mNowWeek >= mVpAdapter.itemCount) 0 else max(mNowWeek, 0)
  }

  /**
   * 设置对应位置的 [mTvWhichWeek] 文字
   */
  protected open fun getWhichWeekStr(position: Int): String {
    return mWhichWeekStr[position]
  }
  
  /**
   * 显示本周的进度
   * @param positionOffset 为 [0.0, 1.0) 的值
   */
  protected open fun showNowWeek(position: Int, positionOffset: Float) {
    val nowWeekPosition = getPositionByNowWeek()
    if (position == nowWeekPosition || position == nowWeekPosition - 1) {
      val offset = if (position == nowWeekPosition) 1 - positionOffset else positionOffset
      mTvNowWeek.alpha = offset
      mTvNowWeek.scaleX = offset
      mTvNowWeek.scaleY = offset
      mBtnBackNowWeek.alpha = 1 - offset
      mBtnBackNowWeek.translationX = offset * (mHeader.width - mBtnBackNowWeek.left)
    } else {
      mTvNowWeek.alpha = 0F
      mTvNowWeek.scaleX = 0F
      mTvNowWeek.scaleY = 0F
      mBtnBackNowWeek.alpha = 1F
      mBtnBackNowWeek.translationX = 0F
    }
    if (position == 0) {
      // 整学期界面不显示“本周”
      // 这里有些小问题，这里是父类，并不知道子类有没有整学期这个界面（目前是都有），先暂时这样吧
      mTvNowWeek.alpha = 0F
    }
  }

  /**
   * 显示课表的 TabLayout，点击第几周按钮后显示
   */
  protected open fun showTabLayout() {
    TransitionManager.beginDelayedTransition(mHeader, Slide(Gravity.START))
    mHeaderTab.visible()
    mHeaderContent.gone()
  }

  /**
   * 隐藏课表的 TabLayout
   */
  protected open fun hideTabLayout() {
    TransitionManager.beginDelayedTransition(mHeader, Slide(Gravity.START))
    mHeaderTab.gone()
    mHeaderContent.visible()
  }
}
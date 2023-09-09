package com.redrock.module_notification.ui.activity

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.editor
import com.mredrock.cyxbs.config.route.NOTIFICATION_HOME
import com.mredrock.cyxbs.config.route.NOTIFICATION_SETTING
import com.mredrock.cyxbs.lib.utils.adapter.FragmentVpAdapter
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.extensions.visible
import com.redrock.module_notification.R
import com.redrock.module_notification.bean.ChangeReadStatusToBean
import com.redrock.module_notification.ui.fragment.ItineraryNotificationFragment
import com.redrock.module_notification.ui.fragment.SysNotificationFragment
import com.redrock.module_notification.ui.fragment.UfieldNotificationFragment
import com.redrock.module_notification.util.Constant.HAS_USER_ENTER_SETTING_PAGE
import com.redrock.module_notification.util.Constant.IS_SWITCH1_SELECT
import com.redrock.module_notification.util.NotificationSp
import com.redrock.module_notification.util.myGetColor
import com.redrock.module_notification.util.noOpDelegate
import com.redrock.module_notification.viewmodel.NotificationViewModel
import com.redrock.module_notification.widget.*
import kotlin.properties.Delegates

@Route(path = NOTIFICATION_HOME)
class NotificationActivity : BaseViewModelActivity<NotificationViewModel>() {
    private var tab3View by Delegates.notNull<View>()  // 行程通知
    private var tab2View by Delegates.notNull<View>()  // 系统通知
    private var tab1View by Delegates.notNull<View>()  // 活动通知

    private val notification_main_container_bg by R.id.notification_main_column_container_background.view<LinearLayout>()
    private val notification_main_container by R.id.notification_main_column_container.view<ConstraintLayout>()

    private val notification_rl_home_back by R.id.notification_rl_home_back.view<RelativeLayout>()
    private val notification_rl_home_dots by R.id.notification_rl_home_dots.view<RelativeLayout>()
    private val notification_home_red_dots by R.id.notification_home_red_dots.view<ImageView>()
    private val notification_home_vp2 by R.id.notification_home_vp2.view<ViewPager2>()
    private val notification_home_tl by R.id.notification_home_tl.view<TabLayout>()
    private val notification_refresh by R.id.notification_refresh.view<VerticalSwipeRefreshLayout>()


    //所有还未读的系统通知消息的id 用来给一键已读使用
    private var allUnreadSysMsgIds = ArrayList<String>()

    //目前ViewPager处于哪个页面
    var whichPageIsIn = 0
        private set

    //是否需要展示
    private var shouldShowRedDots by Delegates.notNull<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification_activity_main)
//        initShadowShape()
        initViewClickListener()
        initVp2()
        initTabLayout()
        initSettingRedDots()
        initObserver()
        initRefreshLayout()
        initMsgData()
        initPageSelect()
    }

    override fun onStart() {
        super.onStart()
        //从webActivity返回时判断活动通知上的小红点是否可以消失
        shouldShowRedDots = NotificationSp.getBoolean(IS_SWITCH1_SELECT, true)
        if (shouldShowRedDots) {
            if (allUnreadSysMsgIds.size != 0)
                changeTabRedDotsVisibility(0, View.VISIBLE)
        } else {
            changeTabRedDotsVisibility(0, View.INVISIBLE)
            changeTabRedDotsVisibility(1, View.INVISIBLE)
            changeTabRedDotsVisibility(2, View.INVISIBLE)
        }
        viewModel.getUFieldActivity()
    }


    private fun initViewClickListener() {
        notification_rl_home_back.setOnSingleClickListener { finish() }
        notification_rl_home_dots.setOnSingleClickListener { enterSettingPage() }
    }

    private fun enterSettingPage() {
        ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
        notification_home_red_dots.visibility = View.INVISIBLE
        NotificationSp.edit { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
    }

    /**
     * 按产品要求取消了弹窗, 该方法弃用, 也许后续更新会用到，暂作保留
     */
    private fun initPopupWindow() {
        when (whichPageIsIn) {
            0 -> {
                var popupWindow by Delegates.notNull<LoadMoreWindow>()

                popupWindow = buildLoadMoreWindow {
                    context = this@NotificationActivity
                    window = this@NotificationActivity.window
                    layoutRes = R.layout.notification_popupwindow_dots_sys
                }

                popupWindow.apply {
                    setOnItemClickListener(R.id.notification_ll_home_popup_fast_read_sys) {
                        viewModel.changeMsgStatus(ChangeReadStatusToBean(allUnreadSysMsgIds))
                        viewModel.changeSysDotStatus(false)
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_delete_read_sys) {
                        DeleteDialog.show(
                            supportFragmentManager,
                            null,
                            tips = "确认要删除所有已读消息吗",
                            onPositiveClick = {
                               /* sysFragment.deleteAllReadMsg() */
                                dismiss()
                            },
                            onNegativeClick = {
                                dismiss()
                            }
                        )
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                        ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
                        notification_home_red_dots.visibility = View.INVISIBLE
                        NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
                    }
                    if (NotificationSp.getBoolean(HAS_USER_ENTER_SETTING_PAGE, false))
                        popupWindow
                            .contentView
                            .findViewById<ImageView>(R.id.notification_iv_popup_setting)
                            .setImageResource(R.drawable.notification_ic_home_setting_normal)

                    popupWindow.showAsDropDown(
                        notification_rl_home_dots,
                        0,
                        dp2px(15.toFloat()),
                        Gravity.END
                    )
                }
            }

            1 -> {
                notification_home_red_dots.visibility = View.INVISIBLE
                NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
                ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
            }/*{
                popupWindow = buildLoadMoreWindow {
                    context = this@NotificationActivity
                    window = this@NotificationActivity.window
                    layoutRes = R.layout.notification_popupwindow_dots_act
                    Height = dp2px(80.toFloat())
                }

                popupWindow.apply {
                    setOnItemClickListener(R.id.notification_ll_home_popup_fast_read_act) {
                        viewModel.changeMsgStatus(ChangeReadStatusToBean(allUnreadActiveMsgIds))
                        viewModel.changeActiveDotStatus(false)
                    }
                    setOnItemClickListener(R.id.notification_ll_home_popup_setting) {
                        ARouter.getInstance().build(NOTIFICATION_SETTING).navigation()
                        notification_home_red_dots.visibility = View.INVISIBLE
                        NotificationSp.editor { putBoolean(HAS_USER_ENTER_SETTING_PAGE, true) }
                    }
                }
            }*/
        }


    }

    private fun initVp2() {
        notification_home_vp2.adapter = FragmentVpAdapter(this)
            .add { UfieldNotificationFragment() }       // whichPageIsIn = 0
            .add { SysNotificationFragment() }          // whichPageIsIn = 1
            .add { ItineraryNotificationFragment() }    // whichPageIsIn = 2
        notification_home_vp2.setPageTransformer(ScaleInTransformer())
        // 因为第一页有左滑删除，所以禁止 vp 滑动
        notification_home_vp2.isUserInputEnabled = false

        notification_home_vp2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                whichPageIsIn = position
                if (position == 2) {
                    changeTabRedDotsVisibility(2, View.INVISIBLE)
                    notification_main_container_bg.visible()
                } else {
                    notification_main_container_bg.gone()
                }
            }
        })

    }

    @SuppressLint("InflateParams")
    private fun initTabLayout() {
        val tabs = arrayOf(
            "活动通知",
            "系统通知",
            "行程通知"
        )
        TabLayoutMediator(
            notification_home_tl,
            notification_home_vp2
        ) { tab, position -> tab.text = tabs[position] }.attach()

        val tab1 = notification_home_tl.getTabAt(0)
        val tab2 = notification_home_tl.getTabAt(1)
        val tab3 = notification_home_tl.getTabAt(2)

        //设置三个tab的自定义View
        tab1View = LayoutInflater.from(this).inflate(R.layout.notification_item_tab1, null)
        tab1?.customView = tab1View
        tab2View = LayoutInflater.from(this).inflate(R.layout.notification_item_tab2, null)
        tab2?.customView = tab2View
        tab3View = LayoutInflater.from(this).inflate(R.layout.notification_item_tab3, null)
        tab3?.customView = tab3View

        //改变文字颜色
        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_selected)))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.notification_tv_tl_tab)
                    ?.setTextColor(ColorStateList.valueOf(myGetColor(R.color.notification_home_tabLayout_text_unselect)))
            }
        }
        notification_home_tl.addOnTabSelectedListener(onTabSelectedListener)
    }

    //当且仅当第一次进入通知模板setting有红点出现
    private fun initSettingRedDots() {
        if (NotificationSp.getBoolean(HAS_USER_ENTER_SETTING_PAGE, false))
            notification_home_red_dots.visibility = View.INVISIBLE
    }

    private fun initObserver() {
        viewModel.itineraryMsg.observe(this) {
            for (item in it.receivedItineraryList) {
                if (!item.hasRead) {
                    viewModel.changeItineraryDotStatus(true)
                    return@observe
                }
            }
            for (item in it.sentItineraryList) {
                if (!item.hasRead) {
                    viewModel.changeItineraryDotStatus(true)
                    return@observe
                }
            }
            viewModel.changeItineraryDotStatus(false)
        }
        viewModel.systemMsg.observe {
            allUnreadSysMsgIds = ArrayList()
            for (value in it!!) {
                if (!value.has_read) {
                    allUnreadSysMsgIds.add(value.id.toString())
                    changeTabRedDotsVisibility(0, View.VISIBLE)
                }
            }
        }

        viewModel.ufieldActivityMsg.observe{
            for (value in it){
                if (!value.clicked){
                    changeTabRedDotsVisibility(1, View.VISIBLE)
                    break
                }
                viewModel.changeActiveDotStatus(false)
            }
        }
        /*viewModel.activeMsg.observe {
            allUnreadActiveMsgIds = ArrayList()
            for (value in it!!) {
                if (!value.has_read) {
                    changeTabRedDotsVisibility(1, View.VISIBLE)
                    allUnreadActiveMsgIds.add(value.id.toString())
                }
            }
        }
*/
        viewModel.popupWindowClickableStatus.observe {
            it?.let { notification_rl_home_dots.isClickable = it }
        }

        //这里通过与Activity同一个viewmodel来与activity通信 控制activity上的TabLayout上的小红点的显示状态
        viewModel.sysDotStatus.observe {
            if (it == true)
                changeTabRedDotsVisibility(1, View.VISIBLE)
            else
                changeTabRedDotsVisibility(1, View.INVISIBLE)
        }
        viewModel.activeDotStatus.observe {
            if (it == true)
                changeTabRedDotsVisibility(0, View.VISIBLE)
            else
                changeTabRedDotsVisibility(0, View.INVISIBLE)
        }
        viewModel.itineraryDotStatus.observe(this) {
            if (it)
                changeTabRedDotsVisibility(2, View.VISIBLE)
            else
                changeTabRedDotsVisibility(2, View.INVISIBLE)
        }

        // 请求（刷新）数据是否成功的监听
        viewModel.getMsgSuccessful.observe {
            notification_refresh.isRefreshing = false
        }

    }

    private fun initRefreshLayout() {
        notification_refresh.setOnRefreshListener {
            viewModel.getAllMsg()
            viewModel.getAllItineraryMsg()
            viewModel.getUFieldActivity()
        }
        notification_refresh.isRefreshing = true
    }

    private fun initMsgData() {
        viewModel.getAllMsg()
        viewModel.getAllItineraryMsg()
    }

    /**
     * 初始化选择哪种类型的通知页面
     * #### 0 为     活动通知页面
     * #### 1 为     系统通知页面
     * #### 2 为     行程通知页面
     * 如下面的进入消息中心后初始显示行程通知页面的写法
     * ```
     * ServiceManager.activity(NOTIFICATION_HOME){
     *      withInt("MsgType", 2)
     * }
     * ```
     */
    private fun initPageSelect() {
        var msgType: Int
        intent.apply {
            msgType = getIntExtra("MsgType",-1)
        }
        if (msgType > -1 && msgType < notification_home_tl.tabCount) {
            notification_home_tl.getTabAt(msgType)?.select()
            intent.removeExtra("MsgType")
        }
    }


    fun removeUnreadSysMsgId(id: String) {
        allUnreadSysMsgIds.remove(id)
        if (allUnreadSysMsgIds.size == 0) {
            changeTabRedDotsVisibility(0, View.INVISIBLE)
        }
    }

    /*    fun removeUnreadActiveMsgIds(id: String) {
            allUnreadActiveMsgIds.remove(id)
            if (allUnreadActiveMsgIds.size == 0) {
                changeTabRedDotsVisibility(1, View.INVISIBLE)
            }
        }*/

    //改变TabLayout小红点的显示状态
    private fun changeTabRedDotsVisibility(position: Int, visibility: Int) {
        if ((visibility != View.INVISIBLE) and (visibility != View.VISIBLE))
            throw Exception("参数只可以是View.INVISIBLE 或者 View.VISIBLE！！！")
        var vis = visibility
        if (!shouldShowRedDots || position == whichPageIsIn)
            vis = View.INVISIBLE
        when (position) {
            0 -> {
                tab1View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                    vis
            }

            1 -> {
                tab2View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                    vis
            }

            2 -> {
                tab3View.findViewById<View>(R.id.notification_iv_tl_red_dots).visibility =
                    vis
            }
        }
    }


    /**
     * 要对某View显示投影（阴影）的一种简易解决方案
     */
    private fun initShadowShape() {
        val shapePathModel = ShapeAppearanceModel.builder()
            // 圆角方案
            .setBottomLeftCorner(RoundedCornerTreatment())
            .setBottomRightCorner(RoundedCornerTreatment())
            // 圆角弧度
            .setBottomLeftCornerSize(16F.dp2pxF)
            .setBottomRightCornerSize(16F.dp2pxF)
            .build()

        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            // 给backgroundDrawable填充颜色
            setTint(Color.parseColor("#27838D"))
            paintStyle = Paint.Style.FILL
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(this@NotificationActivity)
            elevation = 101F.dp2pxF
            // 给backgroundDrawable设置阴影的起始颜色
            setShadowColor(Color.parseColor("#2D538D"))
//            shadowVerticalOffset = 13F.dp2pxF.toInt()
        }
        (notification_main_container.parent as ViewGroup).clipChildren = false
        notification_main_container.background = backgroundDrawable
    }
}
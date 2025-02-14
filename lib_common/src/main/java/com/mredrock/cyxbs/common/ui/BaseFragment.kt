package com.mredrock.cyxbs.common.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.cyxbs.components.account.api.IAccountService
import com.cyxbs.components.utils.service.impl
import com.cyxbs.components.utils.utils.BindView
import com.mredrock.cyxbs.common.mark.ActionLoginStatusSubscriber
import com.mredrock.cyxbs.common.mark.EventBusLifecycleSubscriber
import org.greenrobot.eventbus.EventBus

/**
 * Created By jay68 on 2018/8/9.
 */
open class BaseFragment : Fragment() {


//    /**
//     * 是否打开友盟统计：默认关闭
//     * 当这个fragment被当作一个独立页面时考虑开启
//     * "独立页面"定义：通常情况下一个Activity就是一个页面,但出现以下情况可以将一个fragment当作页面统计
//     * 1. 一个activity中替换不同fragment来显示不同内容
//     * 2. 这个fragment被安置在类似于dialog中或者占据整个页面绝大部分的显示，
//     *    且关闭这个fragment才能进行其他操作，类似dialog
//     *
//     * 若这个fragment满足以上两个条件但是页面的打开关闭并不是严格按照fragment的生命周期
//     * 那么同样不要打开这个统计，否则会出现错误的统计数据，可以手动this调用这两个方法
//     * [fragmentPageOpen]
//     * [fragmentPageClose]
//     * 但是记住使用规则：无论是哪个fragment调用了open方法，必须这个fragment调用了close方法其他fragment才能
//     * 调用open，这里从逻辑上来讲，对于一个你将它定义为一个"页面"的fragment，那么这时候你就可以将它当作一个类似于activity的玩意
//     * 栈顶始终只有一个activity，对吧
//     * 另外可能出现特殊情况，你将一层View当作一个独立页面，而且需要统计这个页面，那么可能需要你自己调用以下两个方法
//     * 并给这个页面命名，不过命名了记得去友盟分析备注一下
//     * [MobclickAgent.onPageStart]
//     * [MobclickAgent.onPageEnd]
//     */
//    protected open val openStatistics = false

    // 只做本地封装使用
    private var baseBundle: Bundle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseBundle = savedInstanceState
        if (this is EventBusLifecycleSubscriber) EventBus.getDefault().register(this)
        val verifyService = IAccountService::class.impl().getVerifyService()
        if (this is ActionLoginStatusSubscriber) {
            if (verifyService.isLogin()) initOnLoginMode(baseBundle)
            if (verifyService.isTouristMode()) initOnTouristMode(baseBundle)
            if (verifyService.isLogin()||verifyService.isTouristMode()) initPage(verifyService.isLogin(),baseBundle)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (this is EventBusLifecycleSubscriber && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this)
        val verifyService = IAccountService::class.impl().getVerifyService()
        if (this is ActionLoginStatusSubscriber) {
            if (verifyService.isLogin()) destroyOnLoginMode()
            if (verifyService.isTouristMode()) destroyOnTouristMode()
            if (verifyService.isLogin()||verifyService.isTouristMode()) destroyPage(verifyService.isLogin())
        }
    }

    @LayoutRes
    open var layoutRes : Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutRes?.let { return inflater.inflate(layoutRes!!, container, false) }
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    

    /**
     * 在简单界面，使用这种方式来得到 View，kae 插件 和 DataBinding/ViewBinding 已不被允许使用
     * ```
     * 使用方法：
     *    val mTvNum: TextView by R.id.xxx.view()
     * or
     *    val mTvNum by R.id.xxx.view<TextView>()
     * ```
     */
    protected fun <T: View> Int.view() = BindView<T>(this, this@BaseFragment)
    
    
    
    
    
    
    
    
    
    
}
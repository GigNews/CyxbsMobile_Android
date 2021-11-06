package com.mredrock.cyxbs.course.widget.page

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.WIDGET_SETTING
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.widget.page.little.LittleConfigActivity
import com.mredrock.cyxbs.course.widget.page.normal.NormalConfigActivity
import com.mredrock.cyxbs.course.widget.page.trans.TransConfigActivity
import kotlinx.android.synthetic.main.widget_activity_config.*


/**
 * Created by zia on 2018/10/11.
 * 设置主页面
 */
@Route(path = WIDGET_SETTING)
class ConfigActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.course_activity_config)

        common_toolbar.init("控件选择")

        widget_config_littleTransLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, TransConfigActivity::class.java))
        }

        widget_config_littleLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, LittleConfigActivity::class.java))
        }

        widget_config_normalLayout.setOnClickListener {
            startActivity(Intent(this@ConfigActivity, NormalConfigActivity::class.java))
        }
    }

}

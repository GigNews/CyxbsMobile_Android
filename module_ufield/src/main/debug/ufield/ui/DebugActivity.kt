package ufield.ui

import android.content.Intent
import android.os.Bundle
import com.mredrock.cyxbs.lib.base.BaseDebugActivity
import com.mredrock.cyxbs.ufield.lyt.ui.CheckActivity
import com.mredrock.cyxbs.ufield.lyt.ui.SearchActivity
import com.mredrock.cyxbs.ufield.lyt.ui.UFieldActivity

/**
 * description ：
 * author : lytMoon
 * email : yytds@foxmail.com
 * date : 2023/8/7 19:53
 * version: 1.0
 */
class DebugActivity : BaseDebugActivity() {
    override val isNeedLogin: Boolean
        get() = true

    override fun onDebugCreate(savedInstanceState: Bundle?) {
        startActivity(Intent(this, UFieldActivity::class.java))
    }
}
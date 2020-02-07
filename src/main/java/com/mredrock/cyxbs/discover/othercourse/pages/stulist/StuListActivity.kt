package com.mredrock.cyxbs.discover.othercourse.pages.stulist

import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.recyclerview.widget.DividerItemDecoration
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.othercourse.R
import com.mredrock.cyxbs.discover.othercourse.databinding.OthercourseDiscoverActivityStuListBinding
import com.mredrock.cyxbs.discover.othercourse.network.Person
import com.mredrock.cyxbs.discover.othercourse.room.STUDENT_TYPE
import kotlinx.android.synthetic.main.othercourse_discover_activity_stu_list.*

class StuListActivity : BaseActivity() {
    override val isFragmentActivity: Boolean
        get() = false
     var title:ObservableField<String> = ObservableField("")
    lateinit var mDataBinding:OthercourseDiscoverActivityStuListBinding
    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding =  DataBindingUtil.setContentView(this,R.layout.othercourse_discover_activity_stu_list)
        mDataBinding.stuListActivity = this
        bottomSheetBehavior = BottomSheetBehavior.from(course_bottom_sheet_content)
        val mStuList = intent.getSerializableExtra("stu_list") as List<Person>

        var title = ""
        if(mStuList.isNotEmpty()){
            title = if(mStuList[0].type == STUDENT_TYPE){
                "选择同学"
            }else{
                "选择老师"
            }
        }
        common_toolbar.initWithSplitLine(title)

        discover_other_course_rv_stu_list.layoutManager = LinearLayoutManager(this)
        discover_other_course_rv_stu_list.adapter = StuListAdater(this,mStuList)
        discover_other_course_rv_stu_list.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setDrawable(getDrawable(R.drawable.discover_other_course_splite_line))
            }

        })
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }else{
            super.onBackPressed()
        }
    }

}

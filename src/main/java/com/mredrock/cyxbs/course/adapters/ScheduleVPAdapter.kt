package com.mredrock.cyxbs.course.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mredrock.cyxbs.course.ui.CourseFragment

/**
 * Created by anriku on 2018/8/16.
 */

class ScheduleVPAdapter(private val mTiles: Array<String>, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragmentList = arrayOfNulls<Fragment>(mTiles.size)

    override fun getItem(position: Int): Fragment {
        if (mFragmentList[position] == null) {
            mFragmentList[position] = CourseFragment().also {
                it.arguments = Bundle().apply {
                    putInt(CourseFragment.WEEK_NUM, position)
                }
            }
        }
        return mFragmentList[position]!!
    }

    override fun getCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package com.mredrock.cyxbs.discover.othercourse.pages.stusearch.fragment

import com.mredrock.cyxbs.discover.othercourse.pages.stusearch.viewmodel.OtherCourseStudentSearchViewModel


class OtherStuCourseSearchFragment : OtherCourseSearchFragment<OtherCourseStudentSearchViewModel>() {
    override val viewModelClass: Class<OtherCourseStudentSearchViewModel> = OtherCourseStudentSearchViewModel::class.java
}

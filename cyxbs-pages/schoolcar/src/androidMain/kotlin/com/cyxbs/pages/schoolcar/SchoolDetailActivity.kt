package com.cyxbs.pages.schoolcar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyxbs.pages.schoolcar.adapter.CarPageAdapter
import com.cyxbs.pages.schoolcar.databinding.SchoolcarActivityDetailSchoolcarBinding
import com.cyxbs.components.base.ui.BaseActivity

/**
 *@Author:SnowOwlet
 *@Date:2022/5/7 12:00
 *
 */
class SchoolDetailActivity: BaseActivity(){
  private lateinit var binding: SchoolcarActivityDetailSchoolcarBinding
  private val vm by viewModels<SchoolDetailViewModel>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = SchoolcarActivityDetailSchoolcarBinding.inflate(layoutInflater)
    setContentView(binding.root)
    vm.mapInfo.observe(this){
      binding.schoolCarDetailRv.apply {
        layoutManager = LinearLayoutManager(this@SchoolDetailActivity)
        this.adapter = CarPageAdapter(this@SchoolDetailActivity,it.lines)
      }
    }
    vm.initMapInfo()
    binding.schoolCarDetailIv.setOnClickListener {
      finish()
    }
  }
}
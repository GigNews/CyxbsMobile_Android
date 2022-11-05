package com.mredrock.cyxbs.course.page.course.utils.container

import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.item.lesson.LinkLesson
import com.mredrock.cyxbs.course.page.course.utils.container.base.ItemPoolController
import com.mredrock.cyxbs.lib.course.fragment.course.expose.container.ICourseContainer
import com.mredrock.cyxbs.lib.course.fragment.course.expose.overlap.IOverlapItem
import com.mredrock.cyxbs.lib.utils.extensions.anim

/**
 * 代理添加 [LinkLesson]
 *
 * - 提供差分刷新方法 [diffRefresh]
 * - 实现了 [LinkLesson] 的回收池用于复用
 * - 监听了使用其他方式删除的 [LinkLesson]，用于解决差分旧数据不同步的问题
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/10 18:22
 */
class LinkLessonContainerProxy(
  val container: ICourseContainer
) : ItemPoolController<LinkLesson, StuLessonData>(container) {
  
  override fun areItemsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areItemsTheSame(oldItem, newItem)
  }
  
  override fun areContentsTheSame(oldItem: StuLessonData, newItem: StuLessonData): Boolean {
    return StuLessonData.areContentsTheSame(oldItem, newItem)
  }
  
  override val itemClass: Class<LinkLesson>
    get() = LinkLesson::class.java
  
  override fun addItem(item: LinkLesson) {
    container.addLesson(item)
  }
  
  override fun removeItem(item: LinkLesson) {
    container.removeLesson(item)
  }
  
  override fun newItem(data: StuLessonData): LinkLesson {
    return LinkLesson(data)
  }
  
  /**
   * 开启关联人 item 的入场动画
   */
  fun startAnimation() {
    /**
     * 注意：因为 [LinkLesson] 是 [IOverlapItem]，它会延迟添加 item，所以需要使用 post
     */
    container.course.post {
      getShowItems().forEachIndexed { index, item ->
        // 因为 Animation 没有公开 clone() 方法，所以只能每次 new 一个新的
        val anim = com.mredrock.cyxbs.lib.course.R.anim.course_anim_entrance.anim
        anim.startOffset = index * 60L
        item.startAnimation(anim)
      }
    }
  }
}
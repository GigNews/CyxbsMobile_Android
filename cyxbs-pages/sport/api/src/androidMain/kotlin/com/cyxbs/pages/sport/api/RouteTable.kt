package com.cyxbs.pages.sport.api

/**
 * 路由表命名规则：
 * <ul>
 *     <li>常量名（全大写）：模块名_功能描述，例：QA_ENTRY</li>
 *     <li>二级路由：/模块名/功能描述，例：/qa/entry</li>
 *     <li>多级路由：/模块依赖关系倒置/功能描述，例：/map/discover/entry</li>
 * </ul>
 */

//体育打卡模块服务
const val SPORT_SERVICE = "/sport/service"
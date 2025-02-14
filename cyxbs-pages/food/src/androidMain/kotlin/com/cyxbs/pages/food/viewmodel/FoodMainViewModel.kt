package com.cyxbs.pages.food.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.cyxbs.pages.food.network.bean.FoodMainBean
import com.cyxbs.pages.food.network.bean.FoodResultBeanItem
import com.cyxbs.pages.food.network.FoodApiService
import com.cyxbs.pages.food.network.bean.FoodPraiseBean
import com.cyxbs.pages.food.network.bean.FoodRefreshBean
import com.cyxbs.components.base.ui.BaseViewModel
import com.cyxbs.components.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FoodMainViewModel : BaseViewModel() {
    init {
        getFoodMain()
    }

    val foodMainBean = MutableLiveData<FoodMainBean>()
    val foodResultBean = MutableLiveData<List<FoodResultBeanItem>>()
    val foodPraiseBean = MutableLiveData<FoodPraiseBean>()
    val foodRefreshBean = MutableLiveData<FoodRefreshBean>()

    //判断第一次请求是否成功，随机生成标记隐藏
    val determineSuccess = MutableLiveData<Boolean>()

    //判断是否选择完标签
    val resultChoose = MutableLiveData<Boolean>()

    //这是返回的信息的list:区域人数特征
    val dataRegion = arrayListOf<String>()
    val dataNumber = arrayListOf<String>()
    var dataFeature = listOf<String>()

    //保存请求的食物列表
    var dataFoodResult = listOf<FoodResultBeanItem>()
    var foodNum = 0

    //这是判断选中的哪些特征的hashMap
    val hashMapRegion = HashMap<String, Boolean>()
    val hashMapNumber = HashMap<String, Boolean>()
    val hashMapFeature = HashMap<String, Boolean>()

    private fun getFoodMain() {
        FoodApiService.INSTANCE.getFoodMain()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                ApiException {

                }.catchOther {
                    toast("网络错误")
                }
            }
            .doOnError {
                toast(it.toString())
            }
            .safeSubscribeBy {
                dataRegion.addAll(it.eatArea)
                dataNumber.addAll(it.eatNum)
                dataFeature = buildList {
                    addAll(it.eatProperty)
                }
                putHashMap(hashMapRegion, dataRegion)
                putHashMap(hashMapNumber, dataNumber)
                putHashMap(hashMapFeature, dataFeature)
                foodMainBean.value = it
            }
    }

    fun postFoodResult() {
        if (hashMapToList(hashMapRegion).isNotEmpty() &&
            hashMapToString(hashMapNumber).isNotEmpty()
        ) {
            FoodApiService.INSTANCE.postFoodResult(
                hashMapToList(hashMapRegion),
                hashMapToString(hashMapNumber),
                hashMapToList(hashMapFeature)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .mapOrInterceptException {
                    //处理没找到
                    ApiException {
                        if (it.status == 10100) {
                            emitter.onSuccess(ArrayList<FoodResultBeanItem>())
                        }
                    }.catchOther {
                        toast("网络错误")
                    }
                }
                .doOnError {
                    toast(it.toString())
                }
                .safeSubscribeBy {
                    if (it.isNotEmpty()) {
                        determineSuccess.value = true
                        dataFoodResult = it
                        foodResultBean.value = it
                    }
                    dataFoodResult = it
                    foodResultBean.value = it
                }
        } else {
            resultChoose.value = false
        }
    }

    fun postFoodPraise(name: String) {
        FoodApiService.INSTANCE.postFoodPraise(name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                ApiException {

                }.catchOther {
                    toast("网络错误")
                }
            }.doOnError {
                toast(it.toString())
            }
            .safeSubscribeBy {
                Log.d("bbp", "postFoodPraise:$it ")
                foodPraiseBean.value = it
            }
    }

    fun postFoodRefresh() {
        FoodApiService.INSTANCE.postFoodRefresh(
            hashMapToList(hashMapRegion),
            hashMapToString(hashMapNumber)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException { }
            .safeSubscribeBy {
                dataFeature = buildList {
                    addAll(it.eatProperty)
                }
                putHashMap(hashMapFeature, dataFeature)
                foodRefreshBean.value = it
            }
    }

    /**
     * 将list转为hashMap，保存状态
     */
    private fun putHashMap(hashMap: HashMap<String, Boolean>, data: List<String>) {
        for (i in data) {
            if (!hashMap.containsKey(i)) hashMap[i] = false
        }
    }

    /**
     * 将保存选择状态的hashMap转为list
     */
    private fun hashMapToList(hashMap: HashMap<String, Boolean>): List<String> {
        return buildList {
            hashMap.forEach { (t, u) ->
                if (u) this.add(t)
            }
        }
    }

    /**
     * HashMap转为String类型
     */
    private fun hashMapToString(hashMap: HashMap<String, Boolean>): String {
        hashMap.forEach { (t, u) ->
            if (u) return t
        }
        return ""
    }


}
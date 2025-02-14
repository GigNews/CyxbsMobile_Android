package com.cyxbs.pages.noclass.util

import android.text.TextUtils

/**
 * ...
 * @author: Black-skyline
 * @email: 2031649401@qq.com
 * @date: 2023/8/18
 * @Description: EditText的输入格式判断（字符串䣌输入格式判断）
 *
 */
object InputFormatUtil {
    fun isNoInput(s: String): Boolean {
        return TextUtils.isEmpty(s.trim())
    }

    /**
     * 检查一个字符串是否包含已定义的标点符号
     * @param s 待检查字符串
     * @return true表示s包含至少一个已定义的标点符号
     */
    fun isIncludePunctuate(s: String): Boolean {
        val punctuateRegex = Regex("\\p{P}")
        val normalSymbol =
            Regex("[`~!@#$^&*()=|{}':;,\\[\\].·<>《》/?！￥…（）—【】‘；：”“。，、？+-/ ]")
        return punctuateRegex.containsMatchIn(s) && normalSymbol.containsMatchIn(s)
    }

    /**
     * 检查一个字符串是否是数字序列
     * @param s 待检查字符串
     * @return true表示s为一个纯数字序列
     */
    fun isNumbersSequence(s: String): Boolean {
        return s.chars().allMatch { Character.isDigit(it) }
    }

    /**
     * 检查一个字符串是否是 纯中文序列 或者 用已定义的连字符连接的中文序列
     * @param s 待检查字符串
     * @return true表示s为一个符合要求的中文序列
     */
    fun isChineseCharacters(s: String): Boolean {
        val chineseRegex = Regex("^[\\u4e00-\\u9fa5]+([·.]?[\\u4e00-\\u9fa5]+)+\$")
        return chineseRegex.matches(s)
    }

    /**
     * 检查一个字符串是否是哪种序列类型
     * @param s 待检查字符串
     * @return 目前1表示纯数字序列，2表示符合要求的中文序列
     */
    fun isWhatType(s: String): Int {
        return if (!isNumbersSequence(s)) {
            if (!isChineseCharacters(s))
                0  // 未在已有字符串标准找到
            else
                2  // 符合要求的中文序列
        } else 1 // 纯数字序列
    }

}
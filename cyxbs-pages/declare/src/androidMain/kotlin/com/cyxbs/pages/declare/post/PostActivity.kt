package com.cyxbs.pages.declare.post

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter.LengthFilter
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.cyxbs.pages.declare.R
import com.cyxbs.pages.declare.databinding.DeclareActivityPostBinding
import com.cyxbs.pages.declare.databinding.DeclareLayoutDialogEditBinding
import com.cyxbs.pages.declare.databinding.DeclareLayoutDialogSubmitBinding
import com.cyxbs.pages.declare.post.adapter.PostSectionRvAdapter
import com.cyxbs.components.base.ui.BaseBindActivity
import com.cyxbs.components.utils.extensions.dp2px
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Optional
import kotlin.coroutines.resume

class PostActivity : BaseBindActivity<DeclareActivityPostBinding>() {

    override val isCancelStatusBar: Boolean
        get() = false

    private val viewModel by viewModels<PostViewModel>()
    private lateinit var submitDialogLayoutBinding: DeclareLayoutDialogSubmitBinding
    private lateinit var editDialogLayoutBinding: DeclareLayoutDialogEditBinding
    private lateinit var submitDialog: MaterialDialog
    private lateinit var editDialog: MaterialDialog
    private lateinit var sectionAdapter: PostSectionRvAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null
        initDialog()
        binding.rvTopic.apply {
            layoutManager = LinearLayoutManager(this@PostActivity)
            adapter = PostSectionRvAdapter(
                onItemTouch = { list, position, et ->
                    lifecycleScope.launch {
                        // 长度限制15
                        openEdit(15, list[position]).ifPresent {
                            if (list.any { s -> it == s }) {
                                toast("不允许存在多个相同的选项哦")
                                return@ifPresent
                            }
                            et.setText(it)
                            list[position] = it
                            // 更新主页按钮状态
                            binding.btnSubmit.active()
                        }
                    }
                },
                onItemUpdate = { binding.btnSubmit.active() }
            ).also { sectionAdapter = it }
        }
        binding.btnSubmit.setOnClickListener {
            if (isPublishable()) {
                // 弹出Dialog
                submitDialog.show()
            }
        }
        binding.etTopic.setOnClickListener {
            lifecycleScope.launch {
                openEdit(30, binding.etTopic.text.toString()).ifPresent {
                    binding.etTopic.setText(it)
                    binding.btnSubmit.active()
                }
            }
        }
        binding.etTopic.isFocusable = false
        binding.declareIvToolbarArrowLeft.setOnClickListener {
            finish()
        }
        lifecycleScope.launch {
            viewModel.postResultFlow.collectLaunch {
                if (it.isSuccess()) {
                    toast("发布成功")
                    finish()
                } else {
                    toast("发布失败 $it")
                }
            }
        }
    }

    private fun initDialog() {
        submitDialogLayoutBinding = DeclareLayoutDialogSubmitBinding.inflate(layoutInflater)
        editDialogLayoutBinding = DeclareLayoutDialogEditBinding.inflate(layoutInflater)
        submitDialog = MaterialDialog(this)
            .customView(view = submitDialogLayoutBinding.root)
            .cornerRadius(literalDp = 8f)
            .maxWidth(literal = 300.dp2px)
        editDialog = MaterialDialog(this)
            .customView(view = editDialogLayoutBinding.root)
            .cornerRadius(literalDp = 8f)
            .maxWidth(literal = 300.dp2px)
        submitDialogLayoutBinding.btnCancel.setOnClickListener {
            submitDialog.hide()
        }
        submitDialogLayoutBinding.btnSubmit.setOnClickListener {
            lifecycleScope.launch {
                // 空白选项不能发
                viewModel.post(binding.etTopic.text.toString(), sectionAdapter.list)
            }
            submitDialog.hide()
        }
    }

    private suspend fun openEdit(maxLen: Int, originText: String = ""): Optional<String> = suspendCancellableCoroutine  { co ->
        editDialogLayoutBinding.apply {
            // 重置edittext状态
            et.setText(originText)
            et.filters = arrayOf(LengthFilter(maxLen))
            textInputLayout.counterMaxLength = maxLen
            btnSubmit.active(et.text.toString().isNotBlank())
            val textWatcher = et.addTextChangedListener {
                btnSubmit.active(!it?.toString().isNullOrBlank())
            }
            fun resetListeners() {
                btnCancel.setOnClickListener(null)
                btnSubmit.setOnClickListener(null)
                et.removeTextChangedListener(textWatcher)
            }
            btnCancel.setOnClickListener {
                editDialog.cancel()
                resetListeners()
                co.resume(Optional.empty())
            }
            btnSubmit.setOnClickListener {
                val str = et.text.toString().replace("\n"," ")
                if (str.isNotBlank()) {
                    editDialog.hide()
                    resetListeners()
                    co.resume(Optional.of(str))
                }
            }
            co.invokeOnCancellation {
                editDialog.cancel()
                resetListeners()
            }
        }
        editDialog.apply {
            setOnCancelListener { co.cancel() }
            show()
        }
    }

    // 发布前的预检
    private fun isPublishable(): Boolean {
        return sectionAdapter.list.all { s -> s.isNotBlank() }
                && !binding.etTopic.text?.toString().isNullOrBlank()
                && sectionAdapter.list.size >= 2
                && sectionAdapter.list.distinct().size == sectionAdapter.list.size
    }

    private fun AppCompatButton.active(active: Boolean = isPublishable()) {
        if (active) {
            setBackgroundResource(R.drawable.declare_ic_btn_background)
        } else {
            setBackgroundResource(R.drawable.declare_ic_btn_background_inactive)
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PostActivity::class.java)
            context.startActivity(starter)
        }
    }
}
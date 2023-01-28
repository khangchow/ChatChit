package com.dhk.chatchit.dialog

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dhk.chatchit.base.BaseDialog
import com.dhk.chatchit.databinding.DialogCustomBinding
import com.dhk.chatchit.extension.show

class CustomDialog : BaseDialog() {
    private var title: String? = null
    private var description: String? = null
    private var positiveButtonText: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonClicked: ((String?) -> Unit)? = null
    private var negativeButtonClicked: (() -> Unit)? = null
    private var isInputModeEnabled: Boolean? = null
    private var isNumberInputType: Boolean? = null
    private var isNeedDismissOnPositiveClicked: Boolean? = null
    private var isNeedDismissOnNegativeClicked: Boolean? = null
    private var inputHint: String? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
        get() = DialogCustomBinding::inflate
    private val binding get() = binding<DialogCustomBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        binding.apply {
            tvTitle.apply {
                title?.let {
                    text = it
                    show()
                }
            }
            tvDescription.apply {
                description?.let {
                    text = it
                    show()
                }
            }
            btnPositive.apply {
                positiveButtonText?.let {
                    text = it
                    show()
                    setOnClickListener {
                        positiveButtonClicked?.invoke(etInput.text.toString())
                        isNeedDismissOnPositiveClicked?.let { required ->
                            if (required) dismiss()
                        }
                    }
                }
            }
            btnNegative.apply {
                negativeButtonText?.let {
                    text = it
                    show()
                    setOnClickListener {
                        negativeButtonClicked?.invoke()
                        isNeedDismissOnNegativeClicked?.let { required ->
                            if (required) dismiss()
                        }
                    }
                }
            }
            isInputModeEnabled?.let {
                if (it) {
                    etInput.apply {
                        show()
                        if (isNumberInputType != null && isNumberInputType == true) {
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                        }
                        if (!inputHint.isNullOrBlank()) hint = inputHint
                    }
                }
            }
        }
    }

    fun setTitle(title: String): CustomDialog {
        this.title = title
        return this
    }

    fun setDescription(description: String): CustomDialog {
        this.description = description
        return this
    }

    fun setPositiveButton(
        text: String,
        buttonClicked: ((String?) -> Unit)? = null
    ): CustomDialog {
        this.positiveButtonText = text
        buttonClicked?.let {
            this.positiveButtonClicked = it
        }
        return this
    }

    fun setNegativeButton(text: String, buttonClicked: (() -> Unit)? = null): CustomDialog {
        this.negativeButtonText = text
        buttonClicked?.let {
            this.negativeButtonClicked = it
        }
        return this
    }

    fun setPositiveButtonDismissOnClicked(isEnabled: Boolean): CustomDialog {
        this.isNeedDismissOnPositiveClicked = isEnabled
        return this
    }

    fun setNegativeButtonDismissOnClicked(isEnabled: Boolean): CustomDialog {
        this.isNeedDismissOnNegativeClicked = isEnabled
        return this
    }

    fun enableInputMode(isEnabled: Boolean): CustomDialog {
        this.isInputModeEnabled = isEnabled
        return this
    }

    fun setHint(hint: String): CustomDialog {
        this.inputHint = hint
        return this
    }

    companion object {
        fun getInstance() = CustomDialog()
    }
}
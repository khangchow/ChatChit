package com.dhk.chatchit.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding

abstract class BaseDialog : DialogFragment() {
    private var _binding: ViewBinding? = null
    open val bindingInflater: ((LayoutInflater, ViewGroup?, Boolean) -> ViewBinding)? = null

    @Suppress("UNCHECKED_CAST")
    protected fun <BINDING : ViewBinding> binding(): BINDING {
        return _binding as BINDING
    }

    private fun setBinding(binding: ViewBinding?) {
        _binding = binding
    }

    override fun onStart() {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.run {
            requestFeature(Window.FEATURE_NO_TITLE)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return bindingInflater?.run {
            setBinding(invoke(inflater, container, false))
            _binding?.root
        } ?: kotlin.run {
            initView(inflater, container, savedInstanceState)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected open fun initView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = null

    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return if (!isAdded) super.show(transaction, tag) else -1
    }

    override fun show(manager: FragmentManager, tag: String?) {
        manager.executePendingTransactions()
        if (!isAdded) super.show(manager, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (!isAdded) super.showNow(manager, tag)
    }
}
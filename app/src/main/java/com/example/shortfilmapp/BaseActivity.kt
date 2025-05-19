package com.example.shortfilmapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

// ui/base/BaseActivity.kt
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    abstract fun inflateViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflateViewBinding()
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    abstract fun setupUI()

    abstract fun observeViewModel()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun DividerItemDecoration(view: RecyclerView, value: Any): RecyclerView.ItemDecoration
}
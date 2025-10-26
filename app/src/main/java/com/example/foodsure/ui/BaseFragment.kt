package com.example.foodsure.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.example.foodsure.AppApplication
import com.example.foodsure.data.ModelRepository

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel>() : Fragment() {
    protected val repository: ModelRepository by lazy { (requireActivity().application as AppApplication).repository }
    private var _binding: VB? = null

    // This property is only valid between onCreateView and onDestroyView.
    protected val binding get() = _binding!!
    protected abstract val viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Inflates the binding for the fragment.
     */
    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * Sets up the unique UI components of the fragment.
     */
    abstract fun setupUI()
}
package com.ladysparks.ttaenggrang.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * BaseFragment 를 생성해서 사용
 * - 매번 프래그먼트마다 반복하던 binding, onCreate, onDestroy 를 작성하지 않아도 괜찮음
 */
abstract class BaseFragment<B : ViewBinding>(private val bind: (View) -> B, @LayoutRes layoutResId: Int) : Fragment(layoutResId) {
    private var _binding: B? = null

    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bind(super.onCreateView(inflater, container, savedInstanceState)!!)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


}

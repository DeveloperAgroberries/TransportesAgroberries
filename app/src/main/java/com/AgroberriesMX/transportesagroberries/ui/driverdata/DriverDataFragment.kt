package com.AgroberriesMX.transportesagroberries.ui.driverdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.AgroberriesMX.transportesagroberries.databinding.FragmentDriverDataBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DriverDataFragment : Fragment() {

    private val driverDataViewModel by viewModels<DriverDataViewModel>()

    private var _binding: FragmentDriverDataBinding? = null

    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverDataBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}
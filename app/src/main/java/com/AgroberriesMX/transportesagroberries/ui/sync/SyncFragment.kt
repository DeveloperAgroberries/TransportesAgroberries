package com.AgroberriesMX.transportesagroberries.ui.sync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.AgroberriesMX.transportesagroberries.databinding.FragmentSyncBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncFragment : Fragment() {

    private val syncViewModel by viewModels<SyncViewModel>()

    private var _binding: FragmentSyncBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSyncBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}
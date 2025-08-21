package com.AgroberriesMX.transportesagroberries.ui.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.databinding.FragmentScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    //private val scannerViewModel by viewModels<ScannerViewModel>()
    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnActivateScanner: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflamos el layout de inicio y lo asignamos al binding
        _binding = FragmentScannerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el listener del botón para navegar al Fragmento del escáner activo.
        // Aquí usamos el ID de la acción directamente en lugar de las clases generadas.
        binding.btnActivateScanner.setOnClickListener {
            findNavController().navigate(R.id.action_scannerFragment_to_scannerActiveFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

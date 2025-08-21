package com.AgroberriesMX.transportesagroberries.ui.scanner.active

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.AgroberriesMX.transportesagroberries.databinding.FragmentScannerActiveBinding
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScannerActiveFragment : Fragment() {

    private var _binding: FragmentScannerActiveBinding? = null
    private val binding get() = _binding!!

    // Vista del escáner
    private lateinit var barcodeView: DecoratedBarcodeView

    // Control manual del estado del flash
    private var isFlashOn = false

    // Conjunto para almacenar los códigos escaneados para evitar duplicados
    private val scannedCodes = mutableSetOf<String>()

    // Variables para evitar el escaneo múltiple
    private var lastScannedCode: String? = null
    private var lastScanTime: Long = 0L

    // BarcodeCallback para manejar los resultados del escaneo
    private val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            result?.text?.let { newCode ->
                val currentTime = System.currentTimeMillis()
                // Evita escaneos duplicados en menos de 1 segundo
                if (newCode != lastScannedCode || (currentTime - lastScanTime) > 1000) {
                    if (scannedCodes.add(newCode)) {
                        updateRegisteredWorkersCount()
                        Toast.makeText(requireContext(), "Código escaneado: $newCode", Toast.LENGTH_SHORT).show()
                    }
                    lastScannedCode = newCode
                    lastScanTime = currentTime
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerActiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = binding.barcodeScannerView
        barcodeView.decodeContinuous(callback)

        binding.btnFlash.setOnClickListener {
            toggleFlash()
        }

        binding.cameraViewContainer.post {
            val animator = ObjectAnimator.ofFloat(
                binding.scanLine,
                View.Y,
                0f,
                binding.cameraViewContainer.height.toFloat()
            )
            animator.duration = 2000
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.repeatMode = ObjectAnimator.REVERSE
            animator.start()
        }

        updateRegisteredWorkersCount()
    }

    // Método para encender o apagar el flash
    private fun toggleFlash() {
        try {
            isFlashOn = !isFlashOn
            if (isFlashOn) {
                barcodeView.setTorchOn()
                Toast.makeText(requireContext(), "Flash: Encendido", Toast.LENGTH_SHORT).show()
            } else {
                barcodeView.setTorchOff()
                Toast.makeText(requireContext(), "Flash: Apagado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al controlar el flash", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para actualizar el conteo en el TextView
    private fun updateRegisteredWorkersCount() {
        binding.tvRegisteredCount.text = scannedCodes.size.toString()
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
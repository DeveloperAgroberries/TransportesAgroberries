package com.AgroberriesMX.transportesagroberries.ui.driverdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.databinding.FragmentDriverDataBinding
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DriverDataFragment : Fragment() {

    // Instancia del ViewModel usando el delegado de Hilt
    private val driverDataViewModel by viewModels<DriverDataViewModel>()

    // Variable para el ViewBinding, usando el patrón de _binding
    private var _binding: FragmentDriverDataBinding? = null

    // Propiedad 'get' para evitar NullPointerExceptions después de onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverDataBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    // onViewCreated es el lugar ideal para configurar las vistas y observar el ViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // **PASO 1: Llamar a la función para obtener las placas sin token**
        driverDataViewModel.getPlacasData()
        driverDataViewModel.getRutasData()


        // **PASO 2: Observar el StateFlow del ViewModel**
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                driverDataViewModel.state.collect { state ->
                    when (state) {
                        is DriverState.Loading -> {
                            // Opcional: Mostrar un indicador de carga
                            // binding.progressBar.visibility = View.VISIBLE
                        }
                        is DriverState.SuccessPlacas -> {
                            // Opcional: Ocultar el indicador de carga
                            // binding.progressBar.visibility = View.GONE

                            // **PASO 3: Llenar el Spinner con los datos**
                            setupPlacasSpinner(state.successPlacas)
                        }
                        is DriverState.SuccessRutas -> {
                            // Opcional: Ocultar el indicador de carga
                            // binding.progressBar.visibility = View.GONE

                            // **PASO 3: Llenar el Spinner con los datos**
                            setupRutasSpinner(state.successRutas)
                        }
                        is DriverState.Error -> {
                            // Opcional: Ocultar el indicador de carga y mostrar el error
                            // binding.progressBar.visibility = View.GONE
                            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Estado inicial o no manejado
                        }
                    }
                }
            }
        }

        // **AÑADIMOS EL LISTENER PARA EL BOTÓN CONFIRMAR**
        binding.btnConfirm.setOnClickListener {
            validateAndNavigate()
        }
    }

    // **Función para configurar el Spinner con la lista de placas**
    private fun setupPlacasSpinner(vehicleList: List<VehicleModel>) {
        // Mapea la lista de objetos VehicleModel a una lista de Strings (las placas)
        val placasList = vehicleList.map { it.cPlacaVeh }

        // Crea el ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            placasList
        )

        // Establece el layout para el menú desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el adaptador al Spinner de tu layout (asumiendo que el ID es spinner_placas)
        binding.spinnerPlacas.adapter = adapter
    }

    // **Función para configurar el Spinner con la lista de placas**
    private fun setupRutasSpinner(routeList: List<RouteModel>) {
        // Mapea la lista de objetos VehicleModel a una lista de Strings (las placas)
        val rutasList = routeList.map { it.vDescripcionRut }

        // Crea el ArrayAdapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            rutasList
        )

        // Establece el layout para el menú desplegable
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el adaptador al Spinner de tu layout (asumiendo que el ID es spinner_placas)
        binding.dataRutas.adapter = adapter
    }

    // **NUEVA FUNCIÓN PARA VALIDAR CAMPOS Y NAVEGAR**
    private fun validateAndNavigate() {
        // Obtenemos los valores seleccionados y el texto ingresado
        val placaSeleccionada = binding.spinnerPlacas.selectedItem as? String
        val rutaSeleccionada = binding.dataRutas.selectedItem as? String
        // El TextInputEditText necesita un ID en el XML, lo he nombrado "etNombreChofer"
        val nombreChofer = binding.etNameDriver.text.toString().trim()

        // Validamos que los tres datos estén llenos
        if (placaSeleccionada != null && rutaSeleccionada != null && nombreChofer.isNotEmpty()) {
            // Si todos los campos están llenos, navegamos al fragmento del escáner
            // Asegúrate de que esta acción esté definida en tu navigation.xml

            if(isNameValid()){
                findNavController().navigate(R.id.scannerFragment)
            }else{
                Toast.makeText(context, "Escribe nombre con al menos 1 apellido.", Toast.LENGTH_LONG).show()
            }
        } else {
            // Si falta algún dato, mostramos un mensaje de error
            Toast.makeText(context, "Por favor, selecciona una placa, una ruta y escribe tu nombre.", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Valida que el nombre de usuario contenga al menos un nombre y un apellido.
     * @return true si el nombre es válido, false en caso contrario.
     */
    private fun isNameValid(): Boolean {
        // Obtiene el texto del campo de nombre y elimina espacios al inicio y al final.
        val name = binding.etNameDriver.text.toString().trim()

        // Divide el nombre en partes (palabras) usando el espacio como separador.
        val nameParts = name.split(" ")

        // La validación verifica si el array tiene 2 o más partes.
        // Esto asegura que haya al menos un nombre y un apellido.
        return if (nameParts.size >= 2) {
            // El nombre es válido, quita cualquier mensaje de error.
            binding.etNameDriver.error = null
            true
        } else {
            // El nombre no es válido (falta el apellido), muestra un mensaje de error.
            binding.etNameDriver.error = "Por favor, introduce tu nombre completo (nombre y apellido)."
            false
        }
    }

    // Es una buena práctica limpiar el binding para evitar fugas de memoria
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

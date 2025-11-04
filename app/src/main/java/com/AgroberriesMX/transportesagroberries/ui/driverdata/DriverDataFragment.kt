package com.AgroberriesMX.transportesagroberries.ui.driverdata

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.AgroberriesMX.transportesagroberries.R
import com.AgroberriesMX.transportesagroberries.data.local.DatabaseHelper
import com.AgroberriesMX.transportesagroberries.databinding.FragmentDriverDataBinding
import com.AgroberriesMX.transportesagroberries.domain.model.RouteModel
import com.AgroberriesMX.transportesagroberries.domain.model.VehicleModel
import com.AgroberriesMX.transportesagroberries.ui.shared.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DriverDataFragment : Fragment() {

    @Inject
    lateinit var dbHelper: DatabaseHelper

    private val driverDataViewModel by viewModels<DriverDataViewModel>()
    private var _binding: FragmentDriverDataBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Declara las listas aquí, a nivel de clase, pero no las inicialices con datos
    private lateinit var vehicleItems: List<SpinnerItem>
    private lateinit var routeItems: List<SpinnerItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDriverDataBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // **Paso 1: Llamar a las funciones para obtener los datos**
        driverDataViewModel.getPlacasData()
        driverDataViewModel.getRutasData()
        driverDataViewModel.getWorkersData()

        // **Paso 2: Observar el StateFlow del ViewModel**
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                driverDataViewModel.state.collect { state ->
                    when (state) {
                        is DriverState.Loading -> {
                            // Puedes mostrar un indicador de carga aquí
                        }
                        is DriverState.SuccessPlacas -> {
                            // Llenar el Spinner de placas con los datos del ViewModel
                            setupPlacasSpinner(state.successPlacas)
                        }
                        is DriverState.SuccessRutas -> {
                            // Llenar el Spinner de rutas con los datos del ViewModel
                            setupRutasSpinner(state.successRutas)
                        }
                        is DriverState.Error -> {
                            Toast.makeText(context, state.error, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Estado inicial o no manejado
                        }
                    }
                }
            }
        }

        // Listener para el campo de texto del nombre del chofer
        binding.etNameDriver.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sharedViewModel.driverName.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Listener para el botón confirmar
        binding.btnConfirm.setOnClickListener {
            validateAndNavigate()
        }
    }

    /*private fun setupPlacasSpinner(vehicleList: List<VehicleModel>) {
        vehicleItems = vehicleList.map {
            SpinnerItem(it.cPlacaVeh, it.cControlVeh.toIntOrNull() ?: 0) // ✅ Correct conversion
        }
        val vehicleNames = vehicleItems.map { it.name }

        val vehicleAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            vehicleNames
        )
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPlacas.adapter = vehicleAdapter

        // Configura el listener con la protección
        binding.spinnerPlacas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (vehicleItems.isNotEmpty() && position >= 0 && position < vehicleItems.size) {
                    val selectedItem = vehicleItems[position]
                    // This line now works correctly as selectedItem.code is an Int
                    sharedViewModel.selectedVehicleCode.value = selectedItem.code
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }*/

    private fun setupPlacasSpinner(vehicleList: List<VehicleModel>) {
        vehicleItems = vehicleList.map {
            // Asegúrate de que el nombre del vehículo sea el valor que quieres mostrar y filtrar
            SpinnerItem(it.cPlacaVeh, it.cControlVeh.toIntOrNull() ?: 0)
        }
        val vehicleNames = vehicleItems.map { it.name }

        // ✅ La clave es usar el layout 'simple_dropdown_item_1line' en el adaptador
        // Este layout está diseñado para funcionar con AutoCompleteTextView y permite la escritura y el filtrado
        val placasAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            vehicleNames
        )

        // Asignar el adaptador al nuevo AutoCompleteTextView
        binding.autoCompleteTextViewPlacas.setAdapter(placasAdapter)

        // Listener para cuando el usuario selecciona un item de la lista filtrada
        binding.autoCompleteTextViewPlacas.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedPlate = parent.getItemAtPosition(position) as String
            val selectedVehicle = vehicleItems.find { it.name == selectedPlate }
            if (selectedVehicle != null) {
                sharedViewModel.selectedVehicleCode.value = selectedVehicle.code
            }
        }

        // Este TextWatcher ya no es necesario si el adaptador es configurado correctamente para el filtrado.
        // El AutoCompleteTextView se encarga de esto.
    }

    /*private fun setupRutasSpinner(routeList: List<RouteModel>) {
        // Mapea la lista para el adaptador
        val spinnerItems = routeList.map {
            SpinnerItem(it.vDescripcionRut, it.cControlRut.toIntOrNull() ?: 0) // ✅ Correct conversion
        }
        val routeNames = spinnerItems.map { it.name }

        val routeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            routeNames
        )
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.dataRutas.adapter = routeAdapter

        // Configura el listener para acceder a la lista original `routeList`
        binding.dataRutas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Protección contra listas vacías
                if (routeList.isNotEmpty() && position >= 0 && position < routeList.size) {
                    // ✅ Declara y usa la variable `selectedRoute` aquí dentro
                    val selectedRoute = routeList[position]

                    // ✅ Aquí puedes usar `selectedRoute` sin problemas
                    // sharedViewModel.selectedRouteCode.value = selectedRoute.cControlRut FORMA INCORRECTA
                    sharedViewModel.selectedRouteCode.value = selectedRoute.cControlRut.toIntOrNull()
                    // sharedViewModel.selectedRouteCost.value = selectedRoute.nCostoRut FORMA INCORRECTA
                    sharedViewModel.selectedRouteCost.value = selectedRoute.nCostoRut.toDoubleOrNull()

                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }*/

    // En la función setupRutasSpinner, el código es similar
    private fun setupRutasSpinner(routeList: List<RouteModel>) {
        val spinnerItems = routeList.map {
            SpinnerItem(it.vDescripcionRut, it.cControlRut.toIntOrNull() ?: 0)
        }
        val routeNames = spinnerItems.map { it.name }

        val rutasAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            routeNames
        )
        binding.autoCompleteTextViewRutas.setAdapter(rutasAdapter)

        binding.autoCompleteTextViewRutas.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val selectedRouteName = parent.getItemAtPosition(position) as String
            val selectedRoute = routeList.find { it.vDescripcionRut == selectedRouteName }

            if (selectedRoute != null) {
                sharedViewModel.selectedRouteCode.value = selectedRoute.cControlRut.toIntOrNull()
                sharedViewModel.selectedRouteCost.value = selectedRoute.nCostoRut.toDoubleOrNull()
            }
        }
    }

    private fun validateAndNavigate() {
        // Obtenemos los valores seleccionados y el texto ingresado
        //val placaSeleccionada = binding.spinnerPlacas.selectedItem as? String
        val placaSeleccionada = binding.autoCompleteTextViewPlacas.text.toString().trim()
        //val rutaSeleccionada = binding.dataRutas.selectedItem as? String
        val rutaSeleccionada = binding.autoCompleteTextViewRutas.text.toString().trim()
        val nombreChofer = binding.etNameDriver.text.toString().trim()

        if (placaSeleccionada != null && rutaSeleccionada != null && nombreChofer.isNotEmpty()) {
            if (isNameValid()) {
                findNavController().navigate(R.id.scannerFragment)
            } else {
                Toast.makeText(context, "Escribe nombre con al menos 1 apellido.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Por favor, selecciona una placa, una ruta y escribe tu nombre.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isNameValid(): Boolean {
        val name = binding.etNameDriver.text.toString().trim()
        val nameParts = name.split(" ")
        return if (nameParts.size >= 2) {
            binding.etNameDriver.error = null
            true
        } else {
            binding.etNameDriver.error = "Por favor, introduce tu nombre completo (nombre y apellido)."
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class SpinnerItem(val name: String, val code: Int)
}
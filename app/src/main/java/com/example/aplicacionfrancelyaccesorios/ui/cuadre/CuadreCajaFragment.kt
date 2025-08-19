package com.example.aplicacionfrancelyaccesorios.ui.cuadre

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplicacionfrancelyaccesorios.R
import com.example.aplicacionfrancelyaccesorios.data.FirebaseService
import java.text.SimpleDateFormat
import java.util.*

class CuadreCajaFragment : Fragment() {

    // Declaración de los componentes de la UI
    private lateinit var textViewFecha: TextView
    private lateinit var textViewEfectivoTotal: TextView
    private lateinit var textViewDesfase: TextView
    private lateinit var buttonSeleccionarFecha: Button

    // Instancia de nuestro servicio de Firebase
    private val firebaseService = FirebaseService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla (crea) la vista del fragmento a partir del layout XML
        val view = inflater.inflate(R.layout.fragment_cuadre_caja, container, false)

        // Asocia las variables con los componentes del layout
        textViewFecha = view.findViewById(R.id.text_view_fecha)
        textViewEfectivoTotal = view.findViewById(R.id.text_view_efectivo_total)
        textViewDesfase = view.findViewById(R.id.text_view_desfase)
        buttonSeleccionarFecha = view.findViewById(R.id.button_seleccionar_fecha)

        // Configura el listener para el botón de fecha
        setupDatePickerButton()

        // Carga los datos del día actual al iniciar la pantalla
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(calendar.time)
        cargarDatos(todayDate)

        return view
    }

    /**
     * Configura la acción del botón para mostrar un selector de fecha (DatePickerDialog).
     */
    private fun setupDatePickerButton() {
        buttonSeleccionarFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                // Formatea la fecha seleccionada al formato yyyy-MM-dd
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)

                // Carga los datos para la fecha seleccionada
                cargarDatos(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
    }

    /**
     * Carga los datos del cuadre de caja desde Firebase para una fecha específica.
     * @param fecha La fecha en formato "yyyy-MM-dd".
     */
    private fun cargarDatos(fecha: String) {
        // Actualiza la UI para mostrar la fecha que se está consultando
        textViewFecha.text = fecha
        // Muestra un estado de "cargando" mientras se obtienen los datos
        textViewEfectivoTotal.text = "Cargando..."
        textViewDesfase.text = "Cargando..."

        firebaseService.getCuadreCaja(fecha) { cuadreCaja ->
            if (cuadreCaja != null) {
                // Si se encontraron datos, se actualiza la UI con ellos
                textViewEfectivoTotal.text = String.format("$%.2f", cuadreCaja.efectivoTotal)
                textViewDesfase.text = String.format("$%.2f", cuadreCaja.desfase)
            } else {
                // Si no se encontraron datos para esa fecha, se muestra un mensaje
                textViewEfectivoTotal.text = "Sin datos"
                textViewDesfase.text = "Sin datos"
                Toast.makeText(context, "No se encontraron registros para el $fecha", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.example.aplicacionfrancelyaccesorios.ui.diario

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aplicacionfrancelyaccesorios.R
import com.example.aplicacionfrancelyaccesorios.data.FirebaseService
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class EstadisticasDiariasFragment : Fragment() {

    // Componentes de la UI
    private lateinit var textViewFecha: TextView
    private lateinit var textViewCompras: TextView
    private lateinit var textViewGastos: TextView
    private lateinit var textViewVentas: TextView
    private lateinit var buttonSeleccionarFecha: Button
    private lateinit var barChart: BarChart

    private val firebaseService = FirebaseService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_diarias, container, false)

        // Inicializar vistas
        textViewFecha = view.findViewById(R.id.text_view_fecha_seleccionada)
        textViewCompras = view.findViewById(R.id.text_view_compras)
        textViewGastos = view.findViewById(R.id.text_view_gastos)
        textViewVentas = view.findViewById(R.id.text_view_ventas)
        buttonSeleccionarFecha = view.findViewById(R.id.button_seleccionar_fecha_diaria)
        barChart = view.findViewById(R.id.bar_chart_diario)

        setupDatePickerButton()
        setupBarChart()

        // Cargar datos del día actual
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(calendar.time)
        cargarDatos(todayDate)

        return view
    }

    /**
     * Configura el botón para abrir el selector de fecha.
     */
    private fun setupDatePickerButton() {
        buttonSeleccionarFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                cargarDatos(formattedDate)
            }, year, month, day).show()
        }
    }

    /**
     * Carga los datos desde Firebase y actualiza la UI.
     * @param fecha La fecha en formato "yyyy-MM-dd".
     */
    private fun cargarDatos(fecha: String) {
        textViewFecha.text = "Estadísticas para: $fecha"
        // Reiniciar textos
        textViewCompras.text = "Cargando..."
        textViewGastos.text = "Cargando..."
        textViewVentas.text = "Cargando..."
        barChart.clear() // Limpiar la gráfica anterior

        firebaseService.getEstadisticasDiarias(fecha) { stats ->
            if (stats != null) {
                // Actualizar textos
                textViewCompras.text = String.format("$%.2f", stats.compras)
                textViewGastos.text = String.format("$%.2f", stats.gastos)
                textViewVentas.text = String.format("$%.2f", stats.ventas)
                // Actualizar gráfica
                actualizarGrafica(stats.compras, stats.gastos, stats.ventas)
            } else {
                textViewCompras.text = "Sin datos"
                textViewGastos.text = "Sin datos"
                textViewVentas.text = "Sin datos"
                Toast.makeText(context, "No hay estadísticas para el $fecha", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Configuración inicial y estilo de la gráfica de barras.
     */
    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawBarShadow(false)
        barChart.setTouchEnabled(false) // Desactivar interacción

        // Configuración del Eje X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Compras", "Gastos", "Ventas")) // Etiquetas

        // Ocultar Eje Y Izquierdo
        barChart.axisLeft.isEnabled = false

        // Configuración del Eje Y Derecho
        barChart.axisRight.apply {
            setDrawGridLines(false)
            axisMinimum = 0f // El valor mínimo siempre es 0
        }

        // Ocultar la leyenda
        barChart.legend.isEnabled = false
    }

    /**
     * Crea y muestra los datos en la gráfica de barras.
     */
    private fun actualizarGrafica(compras: Double, gastos: Double, ventas: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, compras.toFloat()))
        entries.add(BarEntry(1f, gastos.toFloat()))
        entries.add(BarEntry(2f, ventas.toFloat()))

        val dataSet = BarDataSet(entries, "Estadísticas Diarias")

        // Asignar colores personalizados a cada barra
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.brand_dark_1),
            ContextCompat.getColor(requireContext(), R.color.brand_dark_2),
            ContextCompat.getColor(requireContext(), R.color.brand_dark_3)
        )

        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f // Ancho de las barras

        barChart.data = barData
        barChart.invalidate() // Refrescar la gráfica
    }
}
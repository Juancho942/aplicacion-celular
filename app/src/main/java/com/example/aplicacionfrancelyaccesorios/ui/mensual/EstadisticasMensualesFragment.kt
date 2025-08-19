package com.example.aplicacionfrancelyaccesorios.ui.mensual

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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

class EstadisticasMensualesFragment : Fragment() {

    // Componentes de la UI
    private lateinit var spinnerMeses: Spinner
    private lateinit var textViewVentas: TextView
    private lateinit var textViewGastos: TextView
    private lateinit var textViewUtilidad: TextView
    private lateinit var barChart: BarChart

    private val firebaseService = FirebaseService()
    private var isSpinnerInitialSetup = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_mensuales, container, false)

        // Inicializar vistas
        spinnerMeses = view.findViewById(R.id.spinner_meses)
        textViewVentas = view.findViewById(R.id.text_view_ventas_mensuales)
        textViewGastos = view.findViewById(R.id.text_view_gastos_mensuales)
        textViewUtilidad = view.findViewById(R.id.text_view_utilidad_mensual)
        barChart = view.findViewById(R.id.bar_chart_mensual)

        setupBarChart()
        cargarMesesDisponibles()

        return view
    }

    /**
     * Obtiene la lista de meses desde Firebase y la carga en el Spinner.
     */
    private fun cargarMesesDisponibles() {
        firebaseService.getAvailableMonths { meses ->
            if (meses.isNotEmpty() && context != null) {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, meses)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMeses.adapter = adapter

                spinnerMeses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Evitamos que se cargue dos veces al inicio
                        if (isSpinnerInitialSetup) {
                            isSpinnerInitialSetup = false
                            return
                        }
                        val mesSeleccionado = meses[position]
                        cargarDatosDelMes(mesSeleccionado)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                // Cargar datos del primer mes (el más reciente) por defecto
                cargarDatosDelMes(meses[0])

            } else {
                Toast.makeText(context, "No hay datos mensuales disponibles", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Carga las estadísticas para un mes específico desde Firebase.
     * @param mes El mes en formato "yyyy-MM".
     */
    private fun cargarDatosDelMes(mes: String) {
        textViewVentas.text = "Cargando..."
        textViewGastos.text = "Cargando..."
        textViewUtilidad.text = "Cargando..."
        barChart.clear()

        firebaseService.getEstadisticasMensuales(mes) { stats ->
            if (stats != null) {
                textViewVentas.text = String.format("$%.2f", stats.ventas)
                textViewGastos.text = String.format("$%.2f", stats.gastos)
                textViewUtilidad.text = String.format("$%.2f", stats.utilidad)
                actualizarGrafica(stats.ventas, stats.gastos, stats.utilidad)
            } else {
                textViewVentas.text = "Sin datos"
                textViewGastos.text = "Sin datos"
                textViewUtilidad.text = "Sin datos"
                Toast.makeText(context, "No se encontraron datos para $mes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Configuración inicial y estilo de la gráfica de barras.
     */
    private fun setupBarChart() {
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setTouchEnabled(false)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Ventas", "Gastos", "Utilidad"))

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.axisMinimum = 0f

        barChart.legend.isEnabled = false
    }

    /**
     * Crea y muestra los datos en la gráfica de barras.
     */
    private fun actualizarGrafica(ventas: Double, gastos: Double, utilidad: Double) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, ventas.toFloat()))
        entries.add(BarEntry(1f, gastos.toFloat()))
        entries.add(BarEntry(2f, utilidad.toFloat()))

        val dataSet = BarDataSet(entries, "Estadísticas Mensuales")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.brand_dark_1),
            ContextCompat.getColor(requireContext(), R.color.brand_dark_2),
            ContextCompat.getColor(requireContext(), R.color.brand_dark_3)
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        barChart.data = barData
        barChart.invalidate()
    }
}
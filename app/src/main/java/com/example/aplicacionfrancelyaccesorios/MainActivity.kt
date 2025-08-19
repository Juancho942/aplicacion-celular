package com.example.aplicacionfrancelyaccesorios

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.aplicacionfrancelyaccesorios.ui.cuadre.CuadreCajaFragment
import com.example.aplicacionfrancelyaccesorios.ui.diario.EstadisticasDiariasFragment
import com.example.aplicacionfrancelyaccesorios.ui.mensual.EstadisticasMensualesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // Cargar el fragmento inicial
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
            CuadreCajaFragment()).commit()
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null

        when (item.itemId) {
            R.id.nav_cuadre_caja -> selectedFragment = CuadreCajaFragment()
            R.id.nav_stats_diarias -> selectedFragment = EstadisticasDiariasFragment()
            R.id.nav_stats_mensuales -> selectedFragment = EstadisticasMensualesFragment()
        }

        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit()
        }

        true
    }
}
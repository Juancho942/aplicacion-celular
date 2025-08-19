package com.example.aplicacionfrancelyaccesorios.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.aplicacionfrancelyaccesorios.model.CuadreCaja
import com.example.aplicacionfrancelyaccesorios.model.EstadisticasDiarias
import com.example.aplicacionfrancelyaccesorios.model.EstadisticasMensuales

class FirebaseService {

    private val database = FirebaseDatabase.getInstance().getReference()

    fun getCuadreCaja(date: String, callback: (CuadreCaja?) -> Unit) {
        database.child("cuadres_caja").child(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cuadre = snapshot.getValue(CuadreCaja::class.java)
                callback(cuadre)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getEstadisticasDiarias(date: String, callback: (EstadisticasDiarias?) -> Unit) {
        database.child("estadisticas").child("diario").child(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stats = snapshot.getValue(EstadisticasDiarias::class.java)
                callback(stats)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getEstadisticasMensuales(month: String, callback: (EstadisticasMensuales?) -> Unit) {
        database.child("estadisticas").child("mensual").child(month).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stats = snapshot.getValue(EstadisticasMensuales::class.java)
                callback(stats)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun getAvailableMonths(callback: (List<String>) -> Unit) {
        database.child("estadisticas").child("mensual").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val months = snapshot.children.mapNotNull { it.key }
                // Devolvemos la lista ordenada de forma descendente para que el más reciente aparezca primero
                callback(months.sortedDescending())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }
}
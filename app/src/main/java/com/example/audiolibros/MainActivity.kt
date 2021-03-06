package com.example.audiolibros

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.audiolibros.fragments.DetalleFragment
import com.example.audiolibros.fragments.SelectorFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var adaptador: AdaptadorLibrosFiltro
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adaptador = (applicationContext as Aplicacion).adaptador!!
        //Fragments
        if (contenedor_pequeno != null && fragmentManager.findFragmentById(
                        R.id.contenedor_pequeno) == null) {
            val primerFragment = SelectorFragment()
            fragmentManager.beginTransaction()
                    .add(R.id.contenedor_pequeno, primerFragment).commit()
        }
        //Barra de acciones
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //Pestañas
        tabs.addTab(tabs.newTab().setText("Todos"))
        tabs.addTab(tabs.newTab().setText("Nuevos"))
        tabs.addTab(tabs.newTab().setText("Leidos"))
        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 //Todos
                    -> {
                        adaptador.setNovedad(false)
                        adaptador.setLeido(false)
                    }
                    1 //Nuevos
                    -> {
                        adaptador.setNovedad(true)
                        adaptador.setLeido(false)
                    }
                    2 //Leidos
                    -> {
                        adaptador.setNovedad(false)
                        adaptador.setLeido(true)
                    }
                }
                adaptador.notifyDataSetChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        //Botón Flotante
        fab.setOnClickListener { irUltimoVisitado() }
        // Navigation Drawer
        toggle = ActionBarDrawerToggle(this,
                drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer_layout!!.addDrawerListener(toggle!!)
        toggle.syncState()
        toggle.toolbarNavigationClickListener = View.OnClickListener { onBackPressed() }
        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_preferencias) {
            Toast.makeText(this, "Preferencias", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.menu_acerca) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Mensaje de Acerca De")
            builder.setPositiveButton(android.R.string.ok, null)
            builder.create().show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun mostrarDetalle(id: Int) {
        val detalleFragment = fragmentManager.findFragmentById(R.id.detalle_fragment) as? DetalleFragment
        if (detalleFragment != null) {
            detalleFragment.ponInfoLibro(id)
        } else {
            val nuevoFragment = DetalleFragment()
            val args = Bundle()
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id)
            nuevoFragment.arguments = args
            val transaccion = fragmentManager
                    .beginTransaction()
            transaccion.replace(R.id.contenedor_pequeno, nuevoFragment)
            transaccion.addToBackStack(null)
            transaccion.commit()
        }
        val pref = getSharedPreferences(
                "com.example.audiolibros_internal", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("ultimo", id)
        editor.commit()
    }

    fun irUltimoVisitado() {
        val pref = getSharedPreferences(
                "com.example.audiolibros_internal", Context.MODE_PRIVATE)
        val id = pref.getInt("ultimo", -1)
        if (id >= 0) {
            mostrarDetalle(id)
        } else {
            Toast.makeText(this, "Sin última vista", Toast.LENGTH_LONG).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_todos -> {
                adaptador.setGenero("")
                adaptador.notifyDataSetChanged()
            }
            R.id.nav_epico -> {
                adaptador.setGenero(G_EPICO)
                adaptador.notifyDataSetChanged()
            }
            R.id.nav_XIX -> {
                adaptador.setGenero(G_S_XIX)
                adaptador.notifyDataSetChanged()
            }
            R.id.nav_suspense -> {
                adaptador.setGenero(G_SUSPENSE)
                adaptador.notifyDataSetChanged()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun mostrarElementos(mostrar: Boolean) {
        appBarLayout.setExpanded(mostrar)
        toggle.isDrawerIndicatorEnabled = mostrar
        if (mostrar) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            tabs.visibility = View.VISIBLE
        } else {
            tabs.visibility = View.GONE
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

}

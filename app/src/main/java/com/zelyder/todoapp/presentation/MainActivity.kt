package com.zelyder.todoapp.presentation

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.zelyder.todoapp.R
import com.zelyder.todoapp.appComponent
import com.zelyder.todoapp.presentation.core.MainFragmentFactory
import com.zelyder.todoapp.presentation.core.Notifications
import com.zelyder.todoapp.presentation.core.ViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = MainFragmentFactory(viewModelFactory)

        setContentView(R.layout.activity_main)


        if (savedInstanceState == null) {
            val notifications = Notifications()
            notifications.initialize(this)
            notifications.dismissAll()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
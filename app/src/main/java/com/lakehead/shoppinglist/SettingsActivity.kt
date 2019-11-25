package com.lakehead.shoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        signOut.setOnClickListener {
            startActivity(LoginActivity.getLaunchIntent(this))
            FirebaseAuth.getInstance().signOut()
        }
        google_map_button.setOnClickListener {
            startActivity(MapsActivity.getLaunchIntent(this))
        }
    }
}

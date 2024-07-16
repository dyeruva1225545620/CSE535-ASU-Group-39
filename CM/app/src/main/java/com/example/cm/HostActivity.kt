package com.example.cm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        if (savedInstanceState == null) { // Only if there is no previously saved state
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Project5Task1())
                .commit()
        }
    }
}

package com.example.issue5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    companion object {
        const val viewId = 60000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val frameLayout = fragmentContainerView(this, viewId)
            setContentView(frameLayout)
            supportFragmentManager.addFragment(FragmentRoot::class, viewId)
        }
    }
}
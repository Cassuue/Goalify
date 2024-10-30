 package com.example.goalify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.goalify.databinding.ActivityMainBinding

 class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Add the fragment home to the main page
        replaceFragment(Home())

        // Change the fragment depend which item is selected
        binding.bottomNavigationView.setOnItemSelectedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
            if (currentFragment is AddTask && currentFragment.shouldWarnOnExit()) {
                currentFragment.showWarning {
                    when (it.itemId) {

                        R.id.home -> replaceFragment(Home())
                        R.id.rewards -> replaceFragment(Rewards())
                        R.id.calendar -> replaceFragment(Calendar())

                        else -> {

                        }
                    }
                    true
                }
                false
            }
            else{
                when (it.itemId) {

                    R.id.home -> replaceFragment(Home())
                    R.id.rewards -> replaceFragment(Rewards())
                    R.id.calendar -> replaceFragment(Calendar())

                    else -> {

                    }
                }
                true
            }
        }

    }

     private fun replaceFragment(fragment : androidx.fragment.app.Fragment){

         val fragmentManager = supportFragmentManager
         val fragmentTransation = fragmentManager.beginTransaction()
         fragmentTransation.replace(R.id.frameLayout, fragment)
         fragmentTransation.commit()
     }
}
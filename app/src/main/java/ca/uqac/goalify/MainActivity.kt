package ca.uqac.goalify

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import ca.uqac.goalify.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

 class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser !== null) { // TODO: Change !== to ==
            // User is not signed in
            setContentView(R.layout.activity_login)
        } else {
            // User is signed in
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
                            R.id.rewards -> replaceFragment(RewardsFragment())
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
                        R.id.rewards -> replaceFragment(RewardsFragment())
                        R.id.calendar -> replaceFragment(Calendar())

                        else -> {

                        }
                    }
                    true
                }
            }

            binding.materialToolBar.setOnMenuItemClickListener(){
                when (it.itemId){
                    R.id.profile -> replaceFragment(Profile())
                    else -> {

                    }
                }
                true
            }
        }


    }

     private fun replaceFragment(fragment : Fragment){

         val fragmentManager = supportFragmentManager
         val fragmentTransation = fragmentManager.beginTransaction()
         fragmentTransation.replace(R.id.frameLayout, fragment)
         fragmentTransation.commit()
     }
}
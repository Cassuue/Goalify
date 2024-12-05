package ca.uqac.goalify

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import ca.uqac.goalify.databinding.ActivityMainBinding
import ca.uqac.goalify.ui.reward.Reward
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        NavigationUI.setupWithNavController(bottomNav, navController)

        val topBar = binding.materialToolBar

        topBar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        topBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }
        }

        // On définit la langue de base de l'application en français
        val locale = Locale("fr")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)

        // NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Importer les récompenses du fichier JSON dans Firestore
        importRewardsFromJsonToFirestore(this)

        if (!isUserLoggedIn())
            startActivity(Intent(this, AuthActivity::class.java))

        // Ajoutez un écouteur pour les changements de destination de navigation
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_profile) {
                bottomNav.visibility = BottomNavigationView.GONE
            } else {
                bottomNav.visibility = BottomNavigationView.VISIBLE
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    fun importRewardsFromJsonToFirestore(context: Context) {
        val firestore = FirebaseFirestore.getInstance()
        try {
            val inputStream = context.assets.open("rewards.json")
            val reader = InputStreamReader(inputStream)
            val gson = Gson()
            val rewardsType = object : TypeToken<List<Reward>>() {}.type
            val rewards: List<Reward> = gson.fromJson(reader, rewardsType)

            for (reward in rewards) {
                val rewardData = hashMapOf(
                    "title" to reward.title,
                    "subtitle" to reward.subtitle,
                    "imageUrl" to reward.imageUrl,
                    "isUnlocked" to reward.isUnlocked,
                    "isHidden" to reward.isHidden
                )
                firestore.collection("rewards").document(reward.id.toString()).set(rewardData)
                    .addOnSuccessListener {
                        Log.d("FirestoreImport", "Successfully imported reward: ${reward.title}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreImport", "Error importing reward: ${reward.title}", e)
                    }
            }
        } catch (e: Exception) {
            Log.e("FirestoreImport", "Error reading rewards.json", e)
        }
    }
}

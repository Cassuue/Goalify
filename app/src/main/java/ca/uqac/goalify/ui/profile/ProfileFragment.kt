package ca.uqac.goalify.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ca.uqac.goalify.AuthActivity
import ca.uqac.goalify.R
import ca.uqac.goalify.databinding.FragmentProfileBinding
import ca.uqac.goalify.ui.reward.RewardsManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var currentUser: FirebaseUser? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditingObjective = false
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser

        profileViewModel.profileName.observe(viewLifecycleOwner, Observer { name ->
            binding.profileName.text = name
        })

        profileViewModel.profileEmail.observe(viewLifecycleOwner, Observer { email ->
            binding.profileEmail.text = email
        })

        profileViewModel.reloadProfilePhoto.observe(viewLifecycleOwner, Observer { shouldReload ->
            if (shouldReload == true) {
                loadProfilePhoto(userId = currentUser?.uid ?: "")
                profileViewModel.onProfilePhotoReloaded()
            }
        })

        currentUser?.let { user ->
            profileViewModel.updateProfile(user.displayName ?: "", user.email ?: "")
            loadUserObjective(user.uid)
            loadUserAiStatus(user.uid)
        }

        binding.updateProfileButton.setOnClickListener {
            val editProfileDialog = EditProfileDialogFragment()
            editProfileDialog.show(parentFragmentManager, "EditProfileDialog")
        }

        binding.notificationsSwitch.isChecked = loadNotificationState()

        binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveNotificationState(isChecked)
            Toast.makeText(context, "Notifications ${if (isChecked) "activées" else "désactivées"}", Toast.LENGTH_SHORT).show()
        }


        binding.aiSwitch.setOnCheckedChangeListener { _, isChecked ->
            db.collection("users").document(currentUser?.uid ?: "").update("aiEnabled", isChecked)
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Déconnecté", Toast.LENGTH_SHORT).show()
            activity?.finish()
            startActivity(Intent(context, AuthActivity::class.java))
        }

        // Supprimer le compte
        binding.deleteAccountButton.setOnClickListener {
            currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Compte supprimé", Toast.LENGTH_SHORT).show()
                    activity?.finish()
                    startActivity(Intent(context, AuthActivity::class.java))
                } else {
                    Toast.makeText(context, "Erreur lors de la suppression du compte", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.editObjectiveButton.setOnClickListener {
            if (isEditingObjective) {
                val objective = binding.objectiveEditText.text.toString().trim()
                if (objective.isNotEmpty()) {
                    saveObjectiveToFirestore(objective)
                } else {
                    Toast.makeText(context, "Veuillez entrer un objectif", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.objectiveTextView.visibility = View.GONE
                binding.objectiveEditText.visibility = View.VISIBLE
                binding.objectiveEditText.setText(binding.objectiveTextView.text)
                binding.editObjectiveButton.setImageResource(R.drawable.save)
            }
            isEditingObjective = !isEditingObjective
        }
    }

    private fun loadProfilePhoto(userId: String) {
        val currentUser = auth.currentUser
        val file = File(context?.filesDir, "profile_image_$userId.jpg")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.profileImage.setImageBitmap(bitmap)
        } else {
            currentUser?.photoUrl?.let { photoUrl ->
                Glide.with(this).load(photoUrl).into(binding.profileImage)
            } ?: run {
                binding.profileImage.setImageResource(R.drawable.profile_placeholder)
            }
        }
    }

    private fun loadUserObjective(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val objective = document.getString("objective")
                    binding.objectiveTextView.text = objective ?: "Définir votre objectif"
                    binding.objectiveTextView.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erreur lors du chargement de l'objectif", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserAiStatus(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val aiEnabled = document["aiEnabled"] as? Boolean == true
                    binding.aiSwitch.isChecked = aiEnabled
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erreur lors du chargement des paramètres IA", Toast.LENGTH_SHORT).show()
            }
    }
    private fun saveNotificationState(isEnabled: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", 0)
        sharedPreferences.edit()
            .putBoolean("notifications_enabled", isEnabled)
            .apply()
    }

    private fun loadNotificationState(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", 0)
        return sharedPreferences.getBoolean("notifications_enabled", true)
    }

    private fun saveObjectiveToFirestore(objective: String) {
        currentUser?.let { user ->
            val userRef = db.collection("users").document(user.uid)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                if (!snapshot.exists()) {
                    transaction.set(userRef, hashMapOf("objective" to objective), SetOptions.merge())
                } else {
                    transaction.update(userRef, "objective", objective)
                }
            }.addOnSuccessListener {
                Toast.makeText(context, "Objectif enregistré", Toast.LENGTH_SHORT).show()
                binding.objectiveTextView.text = objective
                binding.objectiveTextView.visibility = View.VISIBLE
                binding.objectiveEditText.visibility = View.GONE
                binding.editObjectiveButton.setImageResource(R.drawable.edit)
                isEditingObjective = false

                RewardsManager.checkAndUnlockRewards(user.uid, 2, requireContext())
            }.addOnFailureListener {
                Toast.makeText(context, "Erreur lors de l'enregistrement de l'objectif", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

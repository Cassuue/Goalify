package ca.uqac.goalify.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ca.uqac.goalify.R
import ca.uqac.goalify.databinding.DialogEditProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

class EditProfileDialogFragment : DialogFragment() {

    private var _binding: DialogEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var contextRef: WeakReference<Context>
    private var originalName: String? = null
    private var originalEmail: String? = null
    private var selectedImageUri: Uri? = null
    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        contextRef = WeakReference(context)

        val currentUser = auth.currentUser
        currentUser?.let { user ->
            originalName = user.displayName
            originalEmail = user.email
            binding.editName.setText(originalName)
            binding.editEmail.setText(originalEmail)
            loadProfilePhoto(user.uid)
        }

        binding.selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        binding.saveProfileButton.setOnClickListener {
            val newName = binding.editName.text.toString().trim()
            val newEmail = binding.editEmail.text.toString().trim()

            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                if (newName != originalName || newEmail != originalEmail || selectedImageUri != null) {
                    updateProfile(newName, newEmail)
                } else {
                    showToast("Aucune modification détectée")
                }
            } else {
                showToast("Veuillez remplir tous les champs")
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            binding.profileImageView.setImageURI(selectedImageUri)
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }

    private fun updateProfile(newName: String, newEmail: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (newEmail != originalEmail && newEmail != user.email) {
                            user.verifyBeforeUpdateEmail(newEmail)
                        }
                        if (selectedImageUri != null) {
                            saveProfilePhotoToInternalStorage(selectedImageUri!!)
                        } else {
                            profileViewModel.updateProfile(newName, newEmail)
                            dismiss()
                        }
                    } else {
                        showToast("Erreur lors de la mise à jour du profil")
                    }
                }
        }
    }

    private fun saveProfilePhotoToInternalStorage(imageUri: Uri) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val bitmap = MediaStore.Images.Media.getBitmap(contextRef.get()?.contentResolver, imageUri)
            val file = File(contextRef.get()?.filesDir, "profile_image_${user.uid}.jpg")
            try {
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
                profileViewModel.updateProfile(user.displayName ?: "", user.email ?: "")
                dismiss()
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Erreur lors de l'enregistrement de la photo de profil")
            }
        }
    }

    private fun loadProfilePhoto(userId: String) {
        val currentUser = auth.currentUser
        val file = File(context?.filesDir, "profile_image_$userId.jpg")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.profileImageView.setImageBitmap(bitmap)
        } else {
            currentUser?.photoUrl?.let { photoUrl ->
                Glide.with(this).load(photoUrl).into(binding.profileImageView)
            } ?: run {
                binding.profileImageView.setImageResource(R.drawable.profile_placeholder)
            }
        }
    }

    private fun showToast(message: String) {
        contextRef.get()?.let { context ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package ca.uqac.goalify.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import ca.uqac.goalify.AuthActivity
import ca.uqac.goalify.R
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val signOutButton: Button = view.findViewById(R.id.button_sign_out)
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // Redirect to login activity or any other appropriate action
            activity?.finish()
            startActivity(Intent(activity, AuthActivity::class.java))
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profile().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
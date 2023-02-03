package com.rcappstudio.complaintbox.ui.admin

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.ActivityAdminBinding
import com.rcappstudio.complaintbox.ui.FirebaseData
import com.rcappstudio.complaintbox.ui.FirebaseWorkersData
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModel
import com.rcappstudio.complaintbox.ui.admin.viewmodel.AdminViewModelFactory
import com.rcappstudio.complaintbox.ui.login.LoginActivity
import com.rcappstudio.complaintbox.utils.accountConfirmantionDialog
import com.rcappstudio.complaintbox.utils.initLoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity() {

    private lateinit var binding : ActivityAdminBinding
    @Inject
    lateinit var factory: AdminViewModelFactory

    private lateinit var viewModel: AdminViewModel
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    @Inject
    lateinit var firebaseData: FirebaseData

    @Inject
    lateinit var firebaseWorkersData: FirebaseWorkersData

    private val department by lazy {
        sharedPreferences.getString("department","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)
        init()
        toolBarClickListener()
    }

    private fun init(){
        viewModel = ViewModelProvider(this, factory)[AdminViewModel::class.java]
        viewModel.setNavController(getNavController())
        initBottomNavigation()
        viewModel.switchToFragment(R.id.adminFragment1)
        viewModel.setNotificationToken(department!!)
        initLoadingDialog(this)

    }

    private fun initBottomNavigation() {
        binding.adminBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.admin_pending -> {
                    viewModel.switchToFragment(R.id.adminFragment1)
                }
                R.id.admin_assigned -> {
                    viewModel.switchToFragment(R.id.adminFragment2)
                }
                R.id.admin_solved -> {
                    viewModel.switchToFragment(R.id.adminFragment3)
                }
                R.id.admin_approved -> {
                    viewModel.switchToFragment(R.id.adminFragment4)
                }
            }
            true
        }

        getNavController().addOnDestinationChangedListener{_, dest, _->
            if(dest.id == R.id.viewFragment || dest.id == R.id.mediaViewFragment){
                binding.adminBottomNavigationView.visibility = View.GONE
            } else{
                binding.adminBottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun getNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.adminFragmentContainerView) as NavHostFragment).navController
    }

    private fun toolBarClickListener(){
        binding.toolBar.menu.getItem(0).isVisible = true
        binding.toolBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.addWorker ->
                    openAddWorkerDialog()
                R.id.logout -> {
                    accountConfirmantionDialog(this, "logout", sharedPreferences).show()
                }
                R.id.delete_acc -> {
                    accountConfirmantionDialog(this, "delete", sharedPreferences).show()
                }


            }
            true
        }
    }

    private fun openAddWorkerDialog(){
        val layout = layoutInflater.inflate(R.layout.add_worker,null)
        val name = layout.findViewById<EditText>(R.id.addWorkerName)
        val email = layout.findViewById<EditText>(R.id.addWorkerEmail)
        MaterialAlertDialogBuilder(this)
            .setTitle("Add workers")
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("Add"){_,_->
                if(name.text.toString() !="" && email.toString() != "")
                    createTemporaryAuth(name.text.toString(), email.text.toString())
                else
                    Toast.makeText(this,"Field is empty", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel"){_,_->

            }

            .show()
    }

    private fun createTemporaryAuth(name: String,email: String){

        val firebaseOptions = FirebaseOptions.Builder()
            .setApiKey(getString(R.string.google_api_key))
            .setApplicationId(getString(R.string.project_id)).build();

        var mAuth2 = try {
            val myApp =
                FirebaseApp.initializeApp(this, firebaseOptions, "AnyAppName");
            FirebaseAuth.getInstance(myApp);
        } catch ( e: Exception) {
            FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
        }
        if(mAuth2 != null)
            createWorker(email,mAuth2,name)
    }

    private fun createWorker(email: String, mAuth2 :FirebaseAuth, name: String){
        com.rcappstudio.complaintbox.utils.showDialog()
        mAuth2.createUserWithEmailAndPassword(email,"psnacet")
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"Worker added successfully", Toast.LENGTH_LONG)
                        .show()
                    viewModel.setStaffKeyData(mAuth2.uid.toString(),name)
                    mAuth2!!.signOut()
                    attachCompleteObserver()
                }
            }
            .addOnFailureListener {
                Log.d("TAGData", "createUser2: $it")
                com.rcappstudio.complaintbox.utils.dismissDialog()
                Toast.makeText(this,"Error occurd", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun attachCompleteObserver(){
        viewModel.addWorkerSuccessfulLiveData.observe(this){
            if(it){
                com.rcappstudio.complaintbox.utils.dismissDialog()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
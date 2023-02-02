package com.rcappstudio.complaintbox.ui.viewcomplaint

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentViewBinding
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.model.WorkerData
import com.rcappstudio.complaintbox.notification.NotificationAPI
import com.rcappstudio.complaintbox.ui.FirebaseWorkersData
import com.rcappstudio.complaintbox.ui.adapter.WorkersAdapter
import com.rcappstudio.complaintbox.ui.user.fragments.ImageViewRvAdapter
import com.rcappstudio.complaintbox.utils.getDateTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewFragment : Fragment() {

    val args: ViewFragmentArgs by navArgs()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var factory: ViewViewModelFactory

    @Inject
    lateinit var notificationAPI: NotificationAPI

    private lateinit var department: String


    private lateinit var viewModel : ViewFragmentViewModel

    private lateinit var binding: FragmentViewBinding
    private lateinit var imageRvAdapter: ImageViewRvAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var complaintId: String

    private val isStaff by lazy {
        sharedPref.getBoolean("isStaff", false)
    }

    private val isAdmin by lazy {
        sharedPref.getBoolean("isAdmin", false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewBinding.inflate(layoutInflater, container, false)
        binding.ivViewComplaint.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val comp = Gson().fromJson(args.complaint, Complaint::class.java)
        val departmentList = comp.department!!.split(",")
        val isStaff = sharedPref.getBoolean("isStaff", false)
        val isAdmin = sharedPref.getBoolean("isAdmin", false)
        department = sharedPref.getString("department", "")!!
        complaintId = comp.complaintId.toString()
        Log.d("TAGData", "onViewCreated: ${comp.complaintId}")
        viewModel = ViewModelProvider(this, factory)[ViewFragmentViewModel::class.java]
        viewModel.setNavController(getNavController())

        binding.tvViewComplaintTitle.text = comp.title
        binding.tvViewComplaintDate.text = getDateTime(comp.timeStamp!!)
        binding.tvViewComplaintDesc.text = comp.description
        binding.tvViewComplaintLoc.text = comp.location
        binding.vvViewComplaint.player = exoPlayer
        binding.ivViewComplaint.visibility = View.GONE
        binding.vvViewComplaint.visibility = View.GONE

        if (comp.imageUrlList != null && comp.imageUrlList.isNotEmpty()) {
            binding.ivViewComplaint.visibility = View.VISIBLE
            Log.d("TAGData", "onViewCreated: ${comp.imageUrlList.size}")
            imageRvAdapter = ImageViewRvAdapter(
                requireContext(),
                urlList = comp.imageUrlList as MutableList<String>
            ) { url, position ->
                val map = mutableMapOf<String, String>()
                map["image"] = url
                val directions = ViewFragmentDirections.actionViewFragmentToMediaViewFragment(
                    Gson().toJson(map)
                )
                viewModel.switchToMediaFragment(directions, R.id.mediaViewFragment)
            }
            binding.ivViewComplaint.adapter = imageRvAdapter
        }
        if (comp.videoUrl != null && comp.videoUrl != "") {
            val video = MediaItem.fromUri(comp.videoUrl)
            binding.vvViewComplaint.visibility = View.VISIBLE
            binding.vvViewComplaint.setOnClickListener {
                val map = mutableMapOf<String, String>()
                val gson = Gson()
                map["video"] = comp.videoUrl
                val directions = ViewFragmentDirections.actionViewFragmentToMediaViewFragment(
                    gson.toJson(map)
                )
                viewModel.switchToMediaFragment(directions, R.id.mediaViewFragment)

            }
            exoPlayer.setMediaItem(video)
            exoPlayer.prepare()
//            exoPlayer.play()
        }
        binding.tvViewComplaintDept.text = "Department: ${comp.department}"
        binding.addNote.visibility = View.GONE
        binding.solvedBt.visibility = View.GONE
        binding.tvViewComplaintNote.setText(comp.note.toString())

        if (isAdmin) {

            if (comp.solved == 2) {
                binding.approveBt.visibility = View.VISIBLE
                binding.tvViewComplaintNote.isEnabled = (comp.solved == 2)
                binding.addNote.visibility = View.VISIBLE

                binding.approveBt.setOnClickListener {
                    markApproved(comp.complaintId.toString())
                }

                binding.addNote.setOnClickListener {
                    if (binding.tvViewComplaintNote.text.toString() != "")
                        addNote(
                            binding.tvViewComplaintNote.text.toString(),
                            comp.complaintId.toString()
                        )
                }
            } else if (comp.solved == 0) {
                binding.assignBt.visibility = View.VISIBLE
                binding.assignBt.setOnClickListener {
                    binding.assignView.visibility = View.VISIBLE
                    setBottomSheetRv()
                }
            }
        } else if (
            isStaff
            && department != ""
            && departmentList.contains(department)
            && comp.solved!! == 1
        ) {
            binding.tvViewComplaintNote.isEnabled = (comp.solved == 1)
            binding.solvedBt.visibility = View.VISIBLE
            binding.addNote.visibility = View.VISIBLE

            binding.solvedBt.setOnClickListener {
                markSolved(comp.complaintId.toString())
            }

            binding.addNote.setOnClickListener {
                if (binding.tvViewComplaintNote.text.toString() != "")
                    addNote(
                        binding.tvViewComplaintNote.text.toString(),
                        comp.complaintId.toString()
                    )
            }
        }
    }



    private fun getNavController(): NavController {
        if(isAdmin) return (requireActivity().supportFragmentManager.findFragmentById(R.id.adminFragmentContainerView) as NavHostFragment).navController
        else if(isStaff) return (requireActivity().supportFragmentManager.findFragmentById(R.id.staffFragmentContainerView)as NavHostFragment).navController
        return (requireActivity().supportFragmentManager.findFragmentById(R.id.userFragmentContainerView)as NavHostFragment).navController
    }

    private fun markApproved(complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/solved")
            .setValue(3).addOnCompleteListener {
                requireActivity().onBackPressed()
            }
    }

    private fun addNote(note: String, complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/note")
            .setValue(note).addOnSuccessListener {
                Toast.makeText(requireContext(),"Note added successfully", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markSolved(complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/solved")
            .setValue(2).addOnCompleteListener {
                requireActivity().onBackPressed()
            }
    }

   /* private fun initBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.assignBottomSheet.root)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        setBottomSheetRv()
    }*/

    private fun setBottomSheetRv(){
        binding.workerRv.layoutManager = LinearLayoutManager(requireContext())
        FirebaseWorkersData.liveData.observe(viewLifecycleOwner){
            if(it != null){
                binding.workerRv.adapter = WorkersAdapter(requireContext(),it){uid,token->
                   showAlertDialog(uid,token)
                }
            }
        }
    }

    private fun showAlertDialog(uid: String, token: String){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure to assign the task")
            .setPositiveButton("Ok"){dialog, _ ->
                viewModel.assignTaskToWorker(department,uid,token,notificationAPI,complaintId )
                viewModel.statusChangeSuccessfulLiveData.observe(viewLifecycleOwner){
                    if(it){

                        viewModel.switchBackToFragment(R.id.adminFragment1)
                    }
                }
            }
            .setNegativeButton("Cancel",null)
            .show()
    }



}
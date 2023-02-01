package com.rcappstudio.complaintbox.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.rcappstudio.complaintbox.databinding.FragmentViewBinding
import com.rcappstudio.complaintbox.model.Complaint
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
    private lateinit var binding: FragmentViewBinding
    private lateinit var imageRvAdapter: ImageViewRvAdapter

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
        val department = sharedPref.getString("department", "")

        Log.d("TAGData", "onViewCreated: ${comp.complaintId}")
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
//                Picasso.get().load(url).into(binding?.viewImageFullScr)
            }
            binding.ivViewComplaint.adapter = imageRvAdapter
        }
        if (comp.videoUrl != null && comp.videoUrl != "") {
            val video = MediaItem.fromUri(comp.videoUrl)
            binding.vvViewComplaint.visibility = View.VISIBLE
            exoPlayer.setMediaItem(video)
            exoPlayer.prepare()
            exoPlayer.play()
        }
        binding.tvViewComplaintDept.text = "Department: ${comp.department}"
        binding.addNote.visibility = View.GONE
        binding.solvedBt.visibility = View.GONE
        binding.tvViewComplaintNote.setText(comp.note.toString())

        if (isAdmin) {
            if (comp.solved == 2) {
                binding.approveBt.visibility = View.VISIBLE
                binding.tvViewComplaintNote.isEnabled = (comp.solved == 0)
//            binding.solvedBt.visibility = View.VISIBLE
                binding.addNote.visibility = View.VISIBLE

                binding.approveBt.setOnClickListener {
                    markSolved(comp.complaintId.toString())
                }

                binding.addNote.setOnClickListener {
                    if (binding.tvViewComplaintNote.text.toString() != "")
                        addNote(
                            binding.tvViewComplaintNote.text.toString(),
                            comp.complaintId.toString()
                        )
                }
            } else if (comp.solved==0) {
                binding.assignBt.visibility = View.VISIBLE
                // to open the bottom sheet here
            }
        }

        if (isStaff && department != "" && departmentList.contains(
                department.toString().trim()
            ) && comp.solved!! == 0
        ) {
            binding.tvViewComplaintNote.isEnabled = (comp.solved == 0)
            binding.solvedBt.visibility = View.VISIBLE
            binding.addNote.visibility = View.VISIBLE

            binding.solvedBt.setOnClickListener {
                markAssigned(comp.complaintId.toString())
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

    private fun markAssigned(complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/solved")
            .setValue(1).addOnCompleteListener {
                requireActivity().onBackPressed()
            }
    }

    private fun addNote(note: String, complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/note")
            .setValue(note)
    }

    private fun markSolved(complaintId: String) {
        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId/solved")
            .setValue(2).addOnCompleteListener {
                requireActivity().onBackPressed()
            }
    }
}
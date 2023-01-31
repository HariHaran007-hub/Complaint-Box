package com.rcappstudio.complaintbox.ui.user.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentAddComplaintBinding
import com.rcappstudio.complaintbox.model.Complaint
import com.rcappstudio.complaintbox.utils.dismissDialog
import com.rcappstudio.complaintbox.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddComplaintFragment : Fragment() {

    private lateinit var binding : FragmentAddComplaintBinding
    private lateinit var uriList: MutableList<Uri>
    private val imageUrlList = mutableListOf<String>()

    private val imageViewRvAdapter by lazy {
        ImageViewRvAdapter(requireContext(), uriList){_,_->}
    }
    private var dataCode: Int = 0
    private var department = ""
    private var videoUri: Uri? = null
    private var videoUrl = ""


    @Inject
    lateinit var database: FirebaseDatabase

    private val getImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
            if (dataCode == 1) {
                val thumbnail: Bitmap = it.data!!.extras!!.get("data") as Bitmap
                binding.rvComplaint.visibility = View.VISIBLE
                uriList.add(getImageUri(thumbnail))
                imageViewRvAdapter.notifyDataSetChanged()
            } else if (dataCode == 2) {
                binding.vvComplaint.visibility = View.VISIBLE
                binding.vvComplaint.setVideoURI(it.data!!.data)
                binding.vvComplaint.start()
                videoUri = it.data!!.data
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddComplaintBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener(){
        binding.addImage.setOnClickListener {
            checkForPermission(1)
        }

        binding.addVideo.setOnClickListener {
            checkForPermission(2)
        }
        binding.submit.setOnClickListener {
            extractData()
        }
    }
    private fun checkForPermission(code: Int) {
        Dexter.withContext(requireActivity())
            .withPermissions(listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        if (code == 1) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            dataCode = 1
                            getImage.launch(intent)
                            Toast.makeText(requireContext(),
                                "All permission checked",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                            dataCode = 2
                            getImage.launch(intent)
                        }

                    } else {
                        showRationalDialogForPermissions()
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
    }

    private fun extractData() {
        if (binding.etComplaintTitle.text.toString().isNotEmpty() && department.isNotEmpty()
            && binding.etComplaint.text.toString().isNotEmpty()
            && binding.etLocation.text.toString().isNotEmpty()
        ) {
            initStorageProcess()
        } else {
            Toast.makeText(requireContext(), "Fill all the fields!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun extractChipData() {
        binding.chipGroup.forEach { child ->
            (child as? Chip)?.setOnCheckedChangeListener { _, _ ->
                registerFilterChanged()
            }
        }
    }

    private fun registerFilterChanged() {
        val departmentList = mutableListOf<String>()
        binding.chipGroup.checkedChipIds.forEach { id ->
            departmentList.add(binding.chipGroup.findViewById<Chip>(id).text.toString())
        }
        department = ""
        department = if (departmentList.isNotEmpty()) {
            departmentList.joinToString(", ")
        } else {
            "No Choice"
        }
    }
    private fun initStorageProcess() {
        showDialog()
        if (uriList.size > 0 && videoUri != null) {
            storeImage()
        } else if (uriList.size > 0) {
            storeImage()
        } else if (videoUri != null) {
            storeVideoToCloud()
        } else {
            storeInDatabase()
        }
    }

    private fun storeImage() {
        uriList.forEach {
            FirebaseStorage.getInstance()
                .getReference("File/${Calendar.getInstance().timeInMillis}")
                .putFile(it).addOnSuccessListener { ivSnap ->
                    ivSnap.storage.downloadUrl.addOnSuccessListener { url ->
                        imageUrlList.add(url.toString())
                        if (videoUri != null && imageUrlList.size == uriList.size)
                            storeVideoToCloud()
                        else if (imageUrlList.size == uriList.size)
                            storeInDatabase()
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Failed to post the data please retry!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun storeVideoToCloud() {
        FirebaseStorage.getInstance()
            .getReference("File/${Calendar.getInstance().timeInMillis}")
            .putFile(videoUri!!).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener { url ->
                    videoUrl = url.toString()
                    storeInDatabase()
                }
            }
    }

    private fun storeInDatabase() {
        val title = binding.etComplaintTitle.text.toString().trim()
        val description = binding.etComplaint.text.toString().trim()
        val loc = binding.etLocation.text.toString().trim()
        val departmentList = department
        val time = Calendar.getInstance().timeInMillis
        var vUrl = ""

        if (videoUrl.isNotEmpty())
            vUrl = videoUrl
        val complaintId = FirebaseDatabase.getInstance().getReference("Complaints")
            .push().key

        val complaint = Complaint(
            title = title,
            videoUrl = vUrl,
            description = description,
            department = departmentList,
            timeStamp = time,
            userId = FirebaseAuth.getInstance().uid.toString(),
            location = loc,
            complaintId = complaintId,
            imageUrlList = imageUrlList
        )

        FirebaseDatabase.getInstance().getReference("Complaints/$complaintId")
            .setValue(complaint)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    dismissDialog()
                    requireActivity().onBackPressed()
//                    getNotificationToken()
//                    clearBottomSheet()
                }
            }
    }

    private fun getImageUri(inImage: Bitmap): Uri {
        val tempFile = File.createTempFile("temprentpk", ".png")
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val bitmapData = bytes.toByteArray()

        val fileOutPut = FileOutputStream(tempFile)
        fileOutPut.write(bitmapData)
        fileOutPut.flush()
        fileOutPut.close()
        return Uri.fromFile(tempFile)
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(requireContext()).setMessage("Please enable the required permissions")
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel")
            { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

}
package com.rcappstudio.complaintbox.ui.supportfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.gson.Gson
import com.rcappstudio.complaintbox.R
import com.rcappstudio.complaintbox.databinding.FragmentMediaViewBinding
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaViewFragment : Fragment() {

    val args: MediaViewFragmentArgs by navArgs()

    private lateinit var binding: FragmentMediaViewBinding

    @Inject
    lateinit var exoPlayer: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vvViewMedia.player = exoPlayer
        binding.ivViewMedia.visibility = View.GONE
        binding.vvViewMedia.visibility = View.GONE
        val mediaMap = Gson().fromJson(args.mediaMap, Map::class.java) as Map<String, String>
        val type = if(mediaMap.containsKey("image")) "image" else "video"
        Log.d("TAGData", "onViewCreated: $type")
        val data = mediaMap[type]

        if (type == "image"){
            binding.ivViewMedia.visibility = View.VISIBLE
            Picasso.get()
                .load(data)
                .into(binding.ivViewMedia)
        } else {
            binding.vvViewMedia.visibility = View.VISIBLE
            val video = MediaItem.fromUri(data!!)
            exoPlayer.setMediaItem(video)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }
}
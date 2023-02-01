package com.rcappstudio.complaintbox.ui.supportfragments

import android.os.Bundle
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
import javax.inject.Inject

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
        binding.vvViewMedia.player = exoPlayer
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaMap = Gson().fromJson(args.mediaMap, Map::class.java) as Map<String, String>
        val type = if(mediaMap.containsKey("image")) "image" else "video"
        val data = mediaMap[type]

        if (type == "image"){
            Picasso.get()
                .load(data)
                .fit()
                .centerInside()
                .into(binding.ivViewMedia)
        } else {
            val video = Gson().fromJson(data, MediaItem::class.java)
            exoPlayer.setMediaItem(video)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }
}
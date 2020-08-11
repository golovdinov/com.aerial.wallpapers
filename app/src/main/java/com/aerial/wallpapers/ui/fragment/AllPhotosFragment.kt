package com.aerial.wallpapers.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.aerial.wallpapers.R
import com.aerial.wallpapers.entity.Photo

class AllPhotosFragment : BasePhotosFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_photos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.all_photos_title)
    }

    override fun navigateToBigPhoto(photo: Photo, position: Int) {
        val args = bundleOf("photoPosition" to position)
        findNavController().navigate(R.id.action_allPhotosDest_to_bigPhotosDest, args)
    }
}
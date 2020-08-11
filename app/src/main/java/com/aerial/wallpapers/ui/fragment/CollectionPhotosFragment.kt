package com.aerial.wallpapers.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.aerial.wallpapers.R
import com.aerial.wallpapers.databinding.FragmentCollectionPhotosBinding
import com.aerial.wallpapers.entity.Photo

class CollectionPhotosFragment : BasePhotosFragment() {

    private lateinit var viewBinding: FragmentCollectionPhotosBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentCollectionPhotosBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fix Collapsing Toolbar collapse/expand on resume
        ViewCompat.requestApplyInsets(view.findViewById(R.id.coordinatorLayout))

        arguments?.getString("collectionWallpaperUrl")?.let {
            viewBinding.wallpaperUrl = it
        }

        viewModel.collection.observe(viewLifecycleOwner, Observer {
            it?.let {
                val collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
                collapsingToolbar?.title = it.title
            }
        })
    }

    override fun navigateToBigPhoto(photo: Photo, position: Int) {
        val args = bundleOf(
            "photoPosition" to position,
            "collectionId" to collectionId
        )
        findNavController().navigate(R.id.action_collectionPhotosDest_to_bigPhotosDest, args)
    }

}
package com.aerial.wallpapers.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.aerial.wallpapers.App
import com.aerial.wallpapers.R
import com.aerial.wallpapers.databinding.DialogPurchaseBinding
import com.aerial.wallpapers.di.PersistStorage
import com.aerial.wallpapers.storage.Storage
import com.aerial.wallpapers.viewmodel.PurchaseDialogViewModel
import javax.inject.Inject

class PurchaseDialogFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    @field:PersistStorage lateinit var storage: Storage

    private lateinit var viewBinding: DialogPurchaseBinding
    private lateinit var viewModel: PurchaseDialogViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity?.application as? App)?.appComponent?.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PurchaseDialogViewModel::class.java)

        viewModel.loadPrices()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DialogPurchaseBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lp = dialog?.window?.attributes
        lp?.gravity = Gravity.BOTTOM
        dialog?.window?.attributes = lp

        viewBinding.fragment = this

        viewModel.priceSubscription.observe(viewLifecycleOwner, Observer {
            viewBinding.priceSubscription = it
        })
    }

    override fun getTheme() = R.style.Theme_App_Dialog

    fun subscribe() {
        viewModel.purchaseSubscription(
            activity = activity!!,
            success = {
                storage.put(Storage.KEY_SUBSCRIPTION_PURCHASED, true)

                findNavController().popBackStack()
                //dialog?.hide()
            },
            fail = {

            }
        )
    }

    fun close() {
        findNavController().popBackStack()
        //dialog?.hide()
    }

}
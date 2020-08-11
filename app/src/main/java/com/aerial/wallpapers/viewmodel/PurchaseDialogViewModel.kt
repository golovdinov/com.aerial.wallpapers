package com.aerial.wallpapers.viewmodel

import android.app.Activity
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.aerial.wallpapers.repository.BillingRepository
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseDialogViewModel @Inject constructor(
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val productIdSubscription = MutableLiveData<String>()

    val priceSubscription = productIdSubscription.switchMap { productId ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                val prices = billingRepository.getSubscriptionPrices(listOf(productId))
                emit(prices[productId])
            } catch (e: Throwable) {}
        }
    }

    fun loadPrices() = viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
        try {
            val products = billingRepository.getProductIds()
            productIdSubscription.postValue(products.subscription)
        } catch (e: Throwable) {}
    }

    fun purchaseSubscription(activity: Activity, success: () -> Unit, fail: () -> Unit) = viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
        if (billingRepository.subscribed.value == true) {
            withContext(Dispatchers.Main) {
                success()
            }
        } else {
            productIdSubscription.value?.let {
                billingRepository.subscribe(
                    activity = activity,
                    product = it,
                    callback = { purchased ->
                        viewModelScope.launch(Dispatchers.Main) {
                            when (purchased) {
                                true -> success()
                                false -> fail()
                            }
                        }
                    }
                )
            }
        }
    }

}
package edu.android.thinkr.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author robin
 * Created on 10/10/20
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(AppViewModel::class.java) -> AppViewModel(application)
                else -> throw IllegalArgumentException("ViewModel class (${modelClass.name}) is not mapped")
            }
        } as T
    }
}
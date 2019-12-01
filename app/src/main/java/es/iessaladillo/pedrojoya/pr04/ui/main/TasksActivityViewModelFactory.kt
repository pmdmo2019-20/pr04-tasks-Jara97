package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository

class TasksActivityViewModelFactory(
    private val localRepository: LocalRepository,
    val application: Application
) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TasksActivityViewModel(localRepository,application) as T
    }

}

// TODO: Crea una clase TasksActivityViewModelFactory, que implemente ViewModelProvider.Factory
//  para construir un objeto TasksActivityViewModel

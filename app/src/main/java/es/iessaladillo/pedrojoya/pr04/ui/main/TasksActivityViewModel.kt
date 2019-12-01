package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.Event
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.util.*

class TasksActivityViewModel(private val repository: Repository,
                             private val application: Application) : ViewModel() {

    // Estado de la interfaz


    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _currentFilter: MutableLiveData<TasksActivityFilter> =
        MutableLiveData(TasksActivityFilter.ALL)

    private val _currentFilterMenuItemId: MutableLiveData<Int> =
        MutableLiveData(R.id.mnuFilterAll)
    val currentFilterMenuItemId: LiveData<Int>
        get() = _currentFilterMenuItemId

    private val _activityTitle: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_title_all))
    val activityTitle: LiveData<String>
        get() = _activityTitle

    private val _lblEmptyViewText: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_no_tasks_yet))
    val lblEmptyViewText: LiveData<String>
        get() = _lblEmptyViewText

    // Eventos de comunicación con la actividad

    private val _onStartActivity: MutableLiveData<Event<Intent>> = MutableLiveData()
    val onStartActivity: LiveData<Event<Intent>>
        get() = _onStartActivity

    private val _onShowMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val onShowMessage: LiveData<Event<String>>
        get() = _onShowMessage

    private val _onShowTaskDeleted: MutableLiveData<Event<Task>> = MutableLiveData()
    val onShowTaskDeleted: LiveData<Event<Task>>
        get() = _onShowTaskDeleted

    // ACTION METHODS

    // Hace que se muestre en el RecyclerView todas las tareas.
    fun filterAll() {
        _currentFilter.value=TasksActivityFilter.ALL
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        _currentFilter.value=TasksActivityFilter.COMPLETED
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)

    }

    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        _currentFilter.value=TasksActivityFilter.PENDING
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)

    }

    // Agrega una nueva tarea con dicho concepto. Si la se estaba mostrando
    // la lista de solo las tareas completadas, una vez agregada se debe
    // mostrar en el RecyclerView la lista con todas las tareas, no sólo
    // las completadas.
    fun addTask(concept: String) {
        repository.addTask(concept)
        queryTasks(TasksActivityFilter.ALL)
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
        queryTasks(TasksActivityFilter.ALL)
    }

    // Borra la tarea
    fun deleteTask(task: Task) {
        repository.deleteTask(task.id)
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Borra todas las tareas mostradas actualmente en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que borrar.
    fun deleteTasks() {
        repository.deleteTasks(selectList().map { x->x.id })
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Marca como completadas todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban completadas.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como completadas.
    fun markTasksAsCompleted() {
        repository.markTasksAsCompleted(selectList().map { x->x.id })
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Marca como pendientes todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban pendientes.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como pendientes.
    fun markTasksAsPending() {
        repository.markTasksAsPending(selectList().map { x->x.id })
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Hace que se envíe un Intent con la lista de tareas mostradas actualmente
    // en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un Snackbar indicando
    // que no hay tareas que compartir.
    fun shareTasks() {
        if (_tasks.value?.isNotEmpty() == true) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, _tasks.value?.get(0)?.concept)
                type = "text/plain"
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (isActivityAvailable(application, intent)) {
                application.startActivity(intent)
            }
        } else {
            _onShowMessage.value = Event(application.getString(R.string.tasks_no_tasks_to_share))
        }
    }

    // Actualiza el estado de completitud de la tarea recibida, atendiendo al
    // valor de isCompleted. Si es true la tarea es marcada como completada y
    // en caso contrario es marcada como pendiente.
    fun updateTaskCompletedState(task: Task, isCompleted: Boolean) {
        if(isCompleted){
            repository.markTaskAsPending(task.id)
        }
        else{
            repository.markTaskAsCompleted(task.id)
        }
        queryTasks(_currentFilter.value?:TasksActivityFilter.ALL)
    }

    // Retorna si el concepto recibido es válido (no es una cadena vacía o en blanco)
    fun isValidConcept(concept: String): Boolean {
        return !concept.isBlank()

    }

    // Pide las tareas al repositorio, atendiendo al filtro recibido
    private fun queryTasks(filter: TasksActivityFilter) {
        if(filter == TasksActivityFilter.ALL){
            _tasks.value= repository.queryAllTasks()
            _lblEmptyViewText.value=application.getString(R.string.tasks_no_tasks_yet)
            _activityTitle.value=application.getString(R.string.tasks_title_all)
        }
        else if(filter == TasksActivityFilter.COMPLETED){
            _tasks.value=repository.queryCompletedTasks()
            _lblEmptyViewText.value=application.getString(R.string.tasks_no_completed_tasks_yet)
            _activityTitle.value=application.getString(R.string.tasks_title_completed)

        }
        else if(filter == TasksActivityFilter.PENDING){
            _tasks.value=repository.queryPendingTasks()
            _lblEmptyViewText.value=application.getString(R.string.tasks_no_pending_tasks_yet)
            _activityTitle.value=application.getString(R.string.tasks_title_pending)
        }
    }

        private fun selectList():List<Task>{
        if(_currentFilter.value==TasksActivityFilter.COMPLETED){
            return repository.queryCompletedTasks()
        }
        else if(_currentFilter.value==TasksActivityFilter.PENDING){
            return repository.queryPendingTasks()
        }
        else{
            return repository.queryAllTasks()
        }
    }

   private fun isActivityAvailable(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.applicationContext.packageManager
        val appList = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return appList.size > 0
    }

}


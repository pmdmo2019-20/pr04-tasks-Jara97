package es.iessaladillo.pedrojoya.pr04.data

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.sql.Date
import java.text.DateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class LocalRepository : Repository{

    private var id: Long=0

    private var list:MutableList<Task> = mutableListOf()

    override fun queryAllTasks(): List<Task> {
        return list.sortedByDescending { it.id }
    }

    override fun queryCompletedTasks(): List<Task> {
        return list.filter { x-> x.completed }.sortedByDescending { it.id }
    }

    override fun queryPendingTasks(): List<Task> {
        return list.filter { x-> !x.completed }.sortedByDescending { it.id }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        list.add(Task(id,concept, "Created at "+Date().toLocaleString(),false,""))
        id++
    }

    override fun insertTask(task: Task) {
        list.add(task)
    }


    override fun deleteTask(taskId: Long) {
       list.remove(list.filter {x-> x.id==taskId}.last())
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        list.clear()
    }

    override fun markTaskAsCompleted(taskId: Long) {
        list.filter { x-> x.id==taskId }.forEach{ y->y.completed=true }
    }

    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        list.forEach { x->taskIdList.forEach { y->
            if(x.id==y){
                x.completed=true
            }
        } }
    }

    override fun markTaskAsPending(taskId: Long) {
        list.filter { x-> x.id==taskId }.forEach{y->y.completed=false }
}

    override fun markTasksAsPending(taskIdList: List<Long>) {
        list.forEach { x->taskIdList.forEach { y->
            if(x.id==y){
                x.completed=false
            }
        } }
    }

}

// TODO: Crea una clase llamada LocalRepository que implemente la interfaz Repository
//  usando una lista mutable para almacenar las tareas.
//  Los id de las tareas se irán generando secuencialmente a partir del valor 1 conforme
//  se van agregando tareas (add).


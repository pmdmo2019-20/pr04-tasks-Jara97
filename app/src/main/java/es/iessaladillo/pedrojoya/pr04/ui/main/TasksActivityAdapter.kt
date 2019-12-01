package es.iessaladillo.pedrojoya.pr04.ui.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.strikeThrough
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.tasks_activity_item.*
import java.util.*


class TasksActivityAdapter() : RecyclerView.Adapter<TasksActivityAdapter.ViewHolder>() {

    private var data: List<Task> = emptyList()
    private var onItemClick:((Int)->Unit)?=null
    private var onItemCheck:((Int)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksActivityAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.tasks_activity_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemClick(listener:((Int)->Unit)){
        onItemClick=listener
    }


    fun setOnItemCheck(listener:((Int)->Unit)){
        onItemCheck=listener
    }


    override fun onBindViewHolder(holder: TasksActivityAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun submitList(tasks: List<Task>) {
        data=tasks
        notifyDataSetChanged()
    }

    fun getItem(position:Int): Task {
        return data[position]
    }


    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        init {
            containerView.setOnClickListener{
                onItemClick?.invoke(adapterPosition)
            }
            chkCompleted.setOnClickListener {
                onItemCheck?.invoke(adapterPosition)
            }

        }




        fun bind(task:Task) {
            lblConcept.text = task.concept
            chkCompleted.isChecked=task.completed
            lblConcept.strikeThrough(task.completed)
            if(task.completed){
                task.completedAt= "Completed at "+ Date().toLocaleString()
                viewBar.background=viewBar.resources.getDrawable(R.color.colorCompletedTask)
                lblCompleted.text=task.completedAt
            }
            else{
                lblCompleted.text=task.createdAt
                viewBar.background=viewBar.resources.getDrawable(R.color.colorPendingTask)
            }

        }

    }

}

// TODO: Crea una clase TasksActivityAdapter que actúe como adaptador del RecyclerView
//  y que trabaje con una lista de tareas.
//  Cuando se haga click sobre un elemento se debe cambiar el estado de completitud
//  de la tarea, pasando de completada a pendiente o viceversa.
//  La barra de cada elemento tiene un color distinto dependiendo de si la tarea está
//  completada o no.
//  Debajo del concepto se muestra cuando fue creada la tarea, si la tarea está pendiente,
//  o cuando fue completada si la tarea ya ha sido completada.
//  Si la tarea está completada, el checkBox estará chequeado y el concepto estará tachado.



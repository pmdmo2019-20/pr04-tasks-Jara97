package es.iessaladillo.pedrojoya.pr04.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.hideKeyboard
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import kotlinx.android.synthetic.main.tasks_activity.*


class TasksActivity : AppCompatActivity() {


    private val localRepository=LocalRepository()

    private val listAdapter:TasksActivityAdapter=TasksActivityAdapter().apply {
        this.setOnItemClick { position->
            val task:Task=this.getItem(position)
            viewModel.updateTaskCompletedState(task,task.completed)
        }
        this.setOnItemCheck{ position->
            val task:Task=this.getItem(position)
            viewModel.updateTaskCompletedState(task,task.completed)
        }

    }

    private var mnuFilter: MenuItem? = null


    private val viewModel: TasksActivityViewModel by viewModels{
        TasksActivityViewModelFactory(localRepository,application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupRecyclerView()
        observe()
        setListener()
    }

    private fun setupRecyclerView(){
        lstTasks.run{
            setHasFixedSize(true)
            layoutManager=LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(DividerItemDecoration(context,
                RecyclerView.VERTICAL))
            adapter = listAdapter
        }
    }

    private fun setListener() {
        imgAddTask.setOnClickListener {
            if(viewModel.isValidConcept(txtConcept.text.toString())){
                viewModel.addTask(txtConcept.text.toString())
                it.hideKeyboard()
                txtConcept.setText("")
            }else{
                Snackbar.make(txtConcept, getString(R.string.tasks_creation_error), Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        lstTasks.setOnSwipeListener { viewHolder, _ ->
            val task:Task=this.listAdapter.getItem(viewHolder.adapterPosition)
            viewModel.deleteTask(task)
            Snackbar.make(txtConcept, getString(R.string.tasks_task_deleted,task.concept), Snackbar.LENGTH_LONG)
                .setAction("Undo")    {
                    viewModel.insertTask(task)
                }
                .show()
        }

    }

    private fun observe() {
        viewModel.tasks.observe(this, { showTasks(it) })
        viewModel.activityTitle.observe(this,{ title=it })
        viewModel.lblEmptyViewText.observe(this){ lblEmptyView.text = it }
        viewModel.currentFilterMenuItemId.observe(this){ checkMenuItem(it)}
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> {viewModel.shareTasks() }
            R.id.mnuDelete -> if(viewModel.tasks.value?.size!=0)viewModel.deleteTasks() else Snackbar.make(txtConcept, getString(R.string.tasks_no_tasks_to_delete), Snackbar.LENGTH_LONG).show()
            R.id.mnuComplete -> viewModel.markTasksAsCompleted()
            R.id.mnuPending -> viewModel.markTasksAsPending()
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)
            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(tasks: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(tasks)
            listAdapter.notifyDataSetChanged()
            lblEmptyView.invisibleUnless(tasks.isEmpty())
        }
    }

}




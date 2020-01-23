package org.vmaier.tidfl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_create_task.*
import org.vmaier.tidfl.data.Difficulty
import org.vmaier.tidfl.data.Task
import org.vmaier.tidfl.databinding.ActivityMainBinding
import org.vmaier.tidfl.features.tasks.DatabaseHandler


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        navController = this.findNavController(R.id.nav_host_fragment)
        Log.i("MainActivity", "Bitch I am here!")
    }

    fun createTaskButtonClicked(view : View) {

        val dbHandler = DatabaseHandler(this)
        val goal = create_task_goal.text.toString()
        val details = create_task_details.text.toString()
        val duration = create_task_duration_value.selectedItem.toString().toInt()
        val finalDuration = when (create_task_duration_unit.selectedItem.toString()) {
            "minutes" -> duration
            "hours" -> duration * 60
            "days" -> duration * 60 * 24
            else -> {
                30
            }
        }
        val difficulty : Difficulty = when (create_task_difficulty.selectedItem.toString()) {
            "trivial" -> Difficulty.TRIVIAL
            "regular" -> Difficulty.REGULAR
            "hard" -> Difficulty.HARD
            "insane" -> Difficulty.INSANE
            else -> {
                Difficulty.REGULAR
            }
        }
        val task = Task(goal = goal, details = details, duration = finalDuration, difficulty = difficulty)
        dbHandler.addTask(task)
    }
}

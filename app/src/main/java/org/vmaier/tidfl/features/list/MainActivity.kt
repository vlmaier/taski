package org.vmaier.tidfl.features.list

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.vmaier.tidfl.R


/**
 * Created by Vladas Maier
 * on 09.05.2019
 * at 21:00
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { _ ->
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.add(R.id.fragment_main, CreateTaskFragment())
            transaction.commit()
        }
    }
}

package com.vmaier.taski.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.repository.CategoryRepository


/**
 * Created by Vladas Maier
 * on 30.01.2021
 * at 17:21
 */
class CategoryViewModel(application: Application): AndroidViewModel(application) {

    private var repository = CategoryRepository(application)
    private var categories = repository.getAllLive()

    fun getLive(id: Long): LiveData<Category>? {
        return repository.getLive(id)
    }

    fun getAllLive(): LiveData<MutableList<Category>> {
        return categories
    }

    fun countXP(id: Long): Long {
        return repository.countXP(id)
    }
}
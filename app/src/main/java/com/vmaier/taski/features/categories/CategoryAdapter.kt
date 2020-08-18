package com.vmaier.taski.features.categories

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vmaier.taski.MainActivity
import com.vmaier.taski.R
import com.vmaier.taski.data.AppDatabase
import com.vmaier.taski.data.entity.Category
import com.vmaier.taski.data.entity.Skill
import com.vmaier.taski.utils.Utils
import com.vmaier.taski.views.EditTextDialog
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import timber.log.Timber


/**
 * Created by Vladas Maier
 * on 23/07/2020
 * at 16:47
 */
class CategoryAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var categories: MutableList<Category> = mutableListOf()
    val db = AppDatabase(context)

    enum class ItemViewType(val value: Int) {
        MENU(0),
        CATEGORY(1)
    }

    inner class CategoryMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pickColorView: LinearLayout = itemView.findViewById(R.id.category_pick_color)
        var editNameView: LinearLayout = itemView.findViewById(R.id.category_edit_name)
        var deleteView: LinearLayout = itemView.findViewById(R.id.category_delete)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView: TextView = itemView.findViewById(R.id.category_name)
        var colorView: ImageView = itemView.findViewById(R.id.category_color)
    }

    internal fun setCategories(categories: List<Category>) {
        this.categories = categories.toMutableList()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (categories[position].isMenuShowed) {
            ItemViewType.MENU.value
        } else {
            ItemViewType.CATEGORY.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.MENU.value -> {
                val itemView = inflater.inflate(R.layout.item_category_menu, parent, false)
                CategoryMenuViewHolder(itemView)
            }
            else -> {
                val itemView = inflater.inflate(R.layout.item_category, parent, false)
                CategoryViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            ItemViewType.MENU.value -> setupMenuItemView(viewHolder, position)
            ItemViewType.CATEGORY.value -> setupCategoryItemView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int = categories.size

    private fun setupCategoryItemView(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val category = categories[position]
        val holder = viewHolder as CategoryAdapter.CategoryViewHolder

        // setup "Name" view
        holder.nameView.text = category.name

        // setup "Color" view
        if (category.color != null) {
            holder.colorView.setBackgroundColor(Color.parseColor(category.color))
        } else {
            // set transparent
            holder.colorView.setBackgroundColor(0x00000000)
        }
    }

    private fun setupMenuItemView(viewHolder: RecyclerView.ViewHolder, position: Int) {

        val category = categories[position]
        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
        val resources = context.resources
        val holder = viewHolder as CategoryAdapter.CategoryMenuViewHolder

        // setup "Edit Name" button
        holder.editNameView.setOnClickListener {
            val dialog = EditTextDialog.newInstance(
                title = resources.getString(R.string.heading_edit_category_name),
                hint = resources.getString(R.string.hint_category_name),
                text = category.name,
                positiveButton = R.string.action_set
            )
            dialog.onPositiveButtonClicked = {
                val newName = dialog.editText.text.toString().trim()
                db.categoryDao().updateName(category.id, newName)
                category.name = newName
                notifyItemChanged(position)
                closeMenu()
                Timber.d("Category (${category.id}) name changed.")
            }
            dialog.onNegativeButtonClicked = {
                dialog.dismiss()
            }
            dialog.show(fragmentManager, EditTextDialog::class.simpleName)
        }

        // setup "Pick Color" button
        holder.pickColorView.setOnClickListener {
            val selectedColor = if (category.color != null) Color.parseColor(category.color) else null
            ColorSheet().colorPicker(
                colors = Utils.getMaterialColors(context),
                selectedColor = selectedColor,
                noColorOption = true,
                listener = { color ->
                    val hexColor = if (color == ColorSheet.NO_COLOR) null else ColorSheetUtils.colorToHex(color)
                    db.categoryDao().updateColor(category.id, hexColor)
                    category.color = hexColor
                    notifyItemChanged(position)
                    closeMenu()
                    Timber.d("Category (${category.id}) color changed.")
                })
                .show(fragmentManager)
        }

        // setup "Delete" button
        holder.deleteView.setOnClickListener {
            val countSkills = db.skillDao().countSkillsWithCategory(category.id)
            // show dialog if there are skills attached to category
            if (countSkills > 0) {
                val dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder
                    .setTitle(resources.getString(R.string.alert_category_delete))
                    .setMessage(
                        resources.getQuantityString(
                            R.plurals.alert_assigned_skill, countSkills, countSkills, category.name
                        )
                    )
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.action_proceed_with_delete)) { _, _ ->
                        deleteCategory(position)
                    }
                    .setNegativeButton(resources.getString(R.string.action_cancel)) { dialog, _ ->
                        dialog.cancel()
                        closeMenu()
                    }
                dialogBuilder.create().show()
            } else {
                deleteCategory(position)
            }
        }
    }

    private fun deleteCategory(position: Int) {
        val categoryToRestore = CategoryListFragment.categoryAdapter.removeItem(position)
        closeMenu()
        val message = context.getString(R.string.event_category_deleted)
        // show snackbar with "Undo" option
        val snackbar = Snackbar.make(MainActivity.fab, message, Snackbar.LENGTH_LONG)
            .setAction(context.getString(R.string.action_undo)) {
                // "Undo" is selected -> restore deleted item
                CategoryListFragment.categoryAdapter.restoreItem(categoryToRestore, position)
                closeMenu()
            }
            .setActionTextColor(Utils.getThemeColor(context, R.attr.colorSecondary))
        snackbar.view.setOnClickListener { snackbar.dismiss() }
        snackbar.show()
    }

    private fun removeItem(position: Int): Pair<Category, List<Skill>> {
        val category = categories.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, categories.size)
        val foundSkills = db.skillDao().findSkillsByCategoryId(category.id)
        db.categoryDao().delete(category)
        Timber.d("Category (${category.id}) removed.")
        return Pair(category, foundSkills)
    }

    private fun restoreItem(toRestore: Pair<Category, List<Skill>>, position: Int) {
        categories.add(position, toRestore.first)
        notifyItemInserted(position)
        db.categoryDao().create(toRestore.first)
        toRestore.second.forEach {
            db.skillDao().updateCategoryId(it.id, toRestore.first.id)
        }
        Timber.d("Category (${toRestore.first.id}) restored.")
    }

    fun showMenu(position: Int) {
        for (i in 0 until categories.size) {
            categories[i].isMenuShowed = false
        }
        categories[position].isMenuShowed = true
        notifyDataSetChanged()
    }

    fun isMenuShown(): Boolean {
        for (i in 0 until categories.size) {
            if (categories[i].isMenuShowed) {
                return true
            }
        }
        return false
    }

    fun isMenuShown(position: Int): Boolean {
        return categories[position].isMenuShowed
    }

    fun closeMenu() {
        for (i in 0 until categories.size) {
            categories[i].isMenuShowed = false
        }
        notifyDataSetChanged()
    }

    fun closeMenu(position: Int) {
        categories[position].isMenuShowed = false
        notifyDataSetChanged()
    }
}
package com.example.apitest.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.Section
import com.example.apitest.data.model.User
import com.example.apitest.data.utils.getLoggedInUser
import com.example.apitest.data.utils.loadUsers
import com.example.apitest.data.utils.logoutUser
import com.example.apitest.data.utils.saveLoggedInUser
import com.example.apitest.data.utils.saveUserDarkMode
import com.example.apitest.data.utils.saveUsers
import com.example.apitest.ui.adapter.TasksAdapter
import com.example.apitest.ui.adapter.toDoAdapter
import com.example.apitest.ui.viewmodel.SectionsViewModel
import com.example.apitest.ui.viewmodel.SettingsViewModel
import com.example.apitest.ui.viewmodel.ToDoViewModel

class settings : AppCompatActivity() {
    private lateinit var mySwitch: SwitchCompat
    private lateinit var todoListButton: LinearLayout
    private lateinit var logoutButton: LinearLayout
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var imgText: TextView
    private lateinit var darkmodeIMG: ImageView
    private lateinit var viewModel: SettingsViewModel
    private lateinit var backButton: ImageButton

    private lateinit var usernameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passwordET: EditText
    private lateinit var saveButton: Button
    private lateinit var usernameLogo: ImageView
    private lateinit var emailLogo: ImageView
    private lateinit var passwordLogo: ImageView

    private lateinit var addSectionButton: ImageButton
    private lateinit var tasksAdapter: TasksAdapter

    private var isToDoVisible = false
    private var isAddSectionVisible = false
    private var isOpenDetailsVisible = false

    private lateinit var taskRV: RecyclerView

    private lateinit var toDoViewModel: ToDoViewModel


    private lateinit var sectionsViewModel: SectionsViewModel
    private lateinit var sectionsRV: RecyclerView
    private lateinit var toDoAdapter: toDoAdapter

    private lateinit var detailsTasksRV: RecyclerView
    private lateinit var detailsTasksAdapter: TasksAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        usernameLogo = findViewById(R.id.usernameLogo)
        emailLogo = findViewById(R.id.emailLogo)
        passwordLogo = findViewById(R.id.passwordLogo)

        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        imgText = findViewById(R.id.imageText)

        mySwitch = findViewById(R.id.my_switch)
        darkmodeIMG = findViewById(R.id.darkmodeImage)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        usernameET = findViewById(R.id.usernameET)
        emailET = findViewById(R.id.emailET)
        passwordET = findViewById(R.id.passwordET)

        todoListButton = findViewById(R.id.todolistLayout)

        addSectionButton = findViewById(R.id.plusButton)

        var user = getLoggedInUser(this)
        if (user != null) {
            updateDetails(user)
            viewModel.initDarkMode(this, user.username)
            mySwitch.isChecked = viewModel.isDarkMode.value ?: false
        }


        mySwitch.setOnCheckedChangeListener{_, isChecked ->
            if (user != null)
                viewModel.toggleDarkMode(isChecked, this, user.username)
        }


        viewModel.isDarkMode.observe(this) {isDark ->
            darkMode(isDark)
        }


        logoutButton = findViewById(R.id.logoutLayout)
        logoutButton.setOnClickListener{
            logoutUser(this)
            darkMode(false)
        }

        viewModel.showEditProfile.observe(this) { show ->
            toggle(show, R.id.editProfile)
        }

        findViewById<View>(R.id.editProfileCard).setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }

        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()

            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = loadUsers(this)

            if (users.any { it.username == username && it.username != getLoggedInUser(this)?.username }) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loggedInUser = getLoggedInUser(this)
            if (loggedInUser != null) {
                val index = users.indexOfFirst { it.username == loggedInUser.username }
                if (index != -1) {
                    users[index] = User(username, email, password)
                    saveUsers(this, users)
                    saveLoggedInUser(this, users[index])
                    updateDetails(users[index])
                    viewModel.toggleEditProfileVisibility()
                    passwordET.text.clear()
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }

        todoListButton.setOnClickListener {
            isToDoVisible = !isToDoVisible
            toggle(isToDoVisible, R.id.todoInclude)
        }

        findViewById<ImageButton>(R.id.toDoBack).setOnClickListener{
            toggle(false, R.id.todoInclude)
        }

        addSectionButton.setOnClickListener{
            isAddSectionVisible = !isAddSectionVisible
            toggle(isAddSectionVisible)
        }

        toDoViewModel = ViewModelProvider(this)[ToDoViewModel::class.java]

        taskRV = findViewById(R.id.tasksRV)
        tasksAdapter = TasksAdapter(
            onDeleteClick = { task -> toDoViewModel.removeTask(task) },
            onTitleChanged = { task, newTitle -> toDoViewModel.updateTaskTitle(task, newTitle) }
        )



        taskRV.adapter = tasksAdapter
        taskRV.layoutManager = LinearLayoutManager(this)

        toDoViewModel.tasks.observe(this) { tasks ->
            taskRV.post {
                tasksAdapter.setTasks(tasks)
            }
        }

        findViewById<ImageButton>(R.id.addTaskButton).setOnClickListener{
            toDoViewModel.addTask()
        }

        findViewById<Button>(R.id.cancelButton).setOnClickListener {
            toggle(false)
        }

        val titleET = findViewById<EditText>(R.id.titleET)
        val dateET = findViewById<EditText>(R.id.dateET)
        val categoryET = findViewById<EditText>(R.id.categoryET)
        val addButton = findViewById<Button>(R.id.addButton)


        sectionsViewModel = ViewModelProvider(this)[SectionsViewModel::class.java]

        sectionsViewModel.sections.observe(this) { sections ->
            toDoAdapter.updateSections(sections)
        }

        detailsTasksRV = findViewById(R.id.detailsTasksRV)
        detailsTasksAdapter = TasksAdapter(
            onDeleteClick = { task ->
            },
            onTitleChanged = { task, newTitle ->
            }
        )
        detailsTasksRV.adapter = detailsTasksAdapter
        detailsTasksRV.layoutManager = LinearLayoutManager(this)


        sectionsRV = findViewById(R.id.sectionsRV)
        toDoAdapter = toDoAdapter(emptyList(), object : toDoAdapter.OnItemClickListener {
            override fun onItemClick(section: Section) {
                isOpenDetailsVisible = !isOpenDetailsVisible
                toggle(isOpenDetailsVisible, R.id.detailsInclude)
                updateSectionDetails(section)
            }
        })


        findViewById<ImageButton>(R.id.detailsBack).setOnClickListener{
            toggle(false, R.id.detailsInclude)

        }


        sectionsRV.adapter = toDoAdapter
        sectionsRV.layoutManager = LinearLayoutManager(this)

        addButton.setOnClickListener {
            val title = titleET.text.toString()
            val date = dateET.text.toString()
            val category = categoryET.text.toString()

            if (title.isBlank() || date.isBlank() || category.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val tasks = toDoViewModel.tasks.value ?: emptyList()
            val newSection = Section(title, date, category, tasks)
            sectionsViewModel.addSection(newSection)

            titleET.text.clear()
            dateET.text.clear()
            categoryET.text.clear()
            toDoViewModel.clearTasks()

            toggle(false)
            Toast.makeText(this, "Section added", Toast.LENGTH_SHORT).show()
        }


    }

    private fun updateSectionDetails(section: Section) {
        val detailsTitle = findViewById<TextView>(R.id.detailsTitle)
        val detailsDate = findViewById<TextView>(R.id.detailsDate)
        val detailsCategory = findViewById<TextView>(R.id.detailsCategory)

        detailsTitle.text = section.title
        detailsDate.text = section.date
        detailsCategory.text = section.category

        Log.d("Tasks", "${section.tasks.size}")
        detailsTasksAdapter.setTasks(section.tasks)
    }



    private fun updateDetails(user: User){
        val username = user.username
        val firstInitial = username[0]

        val spaceIndex = username.indexOf(" ")
        val secondInitial = if (spaceIndex != -1 && spaceIndex + 1 < username.length) {
            username[spaceIndex + 1]
        } else {
            null
        }

        val text = if (secondInitial != null) "$firstInitial$secondInitial" else "$firstInitial"

        val initials = text.uppercase()

        imgText.text = initials

        this.username.text = username
        email.text = user.email

        usernameET.setText(user.username)
        emailET.setText(user.email)
    }

    private fun darkMode(isDark: Boolean) {
        val loggedInUser = getLoggedInUser(this)
        if (loggedInUser != null) {
            saveUserDarkMode(this, loggedInUser.username, isDark)
        }

        if (isDark){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkmodeIMG.setImageResource(R.drawable.day)
            usernameLogo.setImageResource(R.drawable.white_usernmae)
            emailLogo.setImageResource(R.drawable.white_email)
            passwordLogo.setImageResource(R.drawable.white_password)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkmodeIMG.setImageResource(R.drawable.night)
            usernameLogo.setImageResource(R.drawable.username)
            emailLogo.setImageResource(R.drawable.mail_24dp_000000_fill1_wght400_grad0_opsz24)
            passwordLogo.setImageResource(R.drawable.password)
        }
    }

    private fun toggle(show: Boolean, viewId: Int){
        val includeLayout = findViewById<View>(viewId)
        val parent = findViewById<RelativeLayout>(R.id.parent)
        val transition: Transition = Slide(Gravity.END)
        transition.duration = 200
        transition.addTarget(includeLayout)
        TransitionManager.beginDelayedTransition(parent, transition)
        includeLayout?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggle(show: Boolean) {
        val includeLayout = findViewById<View>(R.id.addSectionINclude)
        val parent = findViewById<RelativeLayout>(R.id.parent)
        val transition: Transition = Slide(Gravity.BOTTOM).apply {
            duration = 200
            addTarget(includeLayout)
        }

        TransitionManager.beginDelayedTransition(parent, transition)
        includeLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
}
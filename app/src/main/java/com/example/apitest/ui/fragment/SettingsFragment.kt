package com.example.apitest.ui.fragment

import android.os.Bundle
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apitest.R
import com.example.apitest.data.model.Section
import com.example.apitest.data.model.User
import com.example.apitest.data.utils.*
import com.example.apitest.ui.adapter.TasksAdapter
import com.example.apitest.ui.adapter.toDoAdapter
import com.example.apitest.ui.viewmodel.SectionsViewModel
import com.example.apitest.ui.viewmodel.SettingsViewModel
import com.example.apitest.ui.viewmodel.ToDoViewModel

class SettingsFragment : Fragment() {

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
    private lateinit var toDoAdapterInst: toDoAdapter

    private lateinit var detailsTasksRV: RecyclerView
    private lateinit var detailsTasksAdapter: TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // === find views (use 'view.findViewById')
        usernameLogo = view.findViewById(R.id.usernameLogo)
        emailLogo = view.findViewById(R.id.emailLogo)
        passwordLogo = view.findViewById(R.id.passwordLogo)

        username = view.findViewById(R.id.username)
        email = view.findViewById(R.id.email)
        imgText = view.findViewById(R.id.imageText)

        mySwitch = view.findViewById(R.id.my_switch)
        darkmodeIMG = view.findViewById(R.id.darkmodeImage)

        usernameET = view.findViewById(R.id.usernameET)
        emailET = view.findViewById(R.id.emailET)
        passwordET = view.findViewById(R.id.passwordET)

        todoListButton = view.findViewById(R.id.todolistLayout)
        addSectionButton = view.findViewById(R.id.plusButton)

        logoutButton = view.findViewById(R.id.logoutLayout)
        backButton = view.findViewById(R.id.backButton)
        saveButton = view.findViewById(R.id.saveButton)

        val titleET = view.findViewById<EditText>(R.id.titleET)
        val dateET = view.findViewById<EditText>(R.id.dateET)
        val categoryET = view.findViewById<EditText>(R.id.categoryET)
        val addButton = view.findViewById<Button>(R.id.addButton)

        // === ViewModels (fragment scope is fine; use activityViewModels() if you need to share)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        toDoViewModel = ViewModelProvider(this)[ToDoViewModel::class.java]
        sectionsViewModel = ViewModelProvider(this)[SectionsViewModel::class.java]

        // Initialize from stored user
        val user = getLoggedInUser(requireContext())
        if (user != null) {
            updateDetails(user)
            viewModel.initDarkMode(requireContext(), user.username)
            mySwitch.isChecked = viewModel.isDarkMode.value ?: false
        }

        // Dark mode toggle
        mySwitch.setOnCheckedChangeListener { _, isChecked ->
            user?.let { viewModel.toggleDarkMode(isChecked, requireContext(), it.username) }
        }

        viewModel.isDarkMode.observe(viewLifecycleOwner) { isDark ->
            darkMode(isDark)
        }

        // Logout
        logoutButton.setOnClickListener {
            logoutUser(requireContext())
            darkMode(false)
            Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
        }

        // Edit profile sheet
        viewModel.showEditProfile.observe(viewLifecycleOwner) { show ->
            toggle(show, R.id.editProfile)
        }
        view.findViewById<View>(R.id.editProfileCard).setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }
        backButton.setOnClickListener {
            viewModel.toggleEditProfileVisibility()
        }

        // Save profile
        saveButton.setOnClickListener {
            val newUsername = usernameET.text.toString()
            val newEmail = emailET.text.toString()
            val newPassword = passwordET.text.toString()

            if (newUsername.isEmpty() || newPassword.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val users = loadUsers(requireContext())
            val currentUser = getLoggedInUser(requireContext())

            if (users.any { it.username == newUsername && it.username != currentUser?.username }) {
                Toast.makeText(requireContext(), "Username already exists", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (currentUser != null) {
                val index = users.indexOfFirst { it.username == currentUser.username }
                if (index != -1) {
                    users[index] = User(newUsername, newEmail, newPassword)
                    saveUsers(requireContext(), users)
                    saveLoggedInUser(requireContext(), users[index])
                    updateDetails(users[index])
                    viewModel.toggleEditProfileVisibility()
                    passwordET.text.clear()
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ToDo overlay
        todoListButton.setOnClickListener {
            isToDoVisible = !isToDoVisible
            toggle(isToDoVisible, R.id.todoInclude)
        }
        view.findViewById<ImageButton>(R.id.toDoBack).setOnClickListener {
            toggle(false, R.id.todoInclude)
        }

        // Add Section bottom sheet
        addSectionButton.setOnClickListener {
            isAddSectionVisible = !isAddSectionVisible
            toggleBottom(isAddSectionVisible)
        }
        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            toggleBottom(false)
        }

        // Tasks RV (inside Add Section)
        taskRV = view.findViewById(R.id.tasksRV)
        tasksAdapter = TasksAdapter(
            onDeleteClick = { task -> toDoViewModel.removeTask(task) },
            onTitleChanged = { task, newTitle -> toDoViewModel.updateTaskTitle(task, newTitle) }
        )
        taskRV.adapter = tasksAdapter
        taskRV.layoutManager = LinearLayoutManager(requireContext())

        toDoViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskRV.post { tasksAdapter.setTasks(tasks) }
        }

        view.findViewById<ImageButton>(R.id.addTaskButton).setOnClickListener {
            toDoViewModel.addTask()
        }

        // Sections RV
        sectionsRV = view.findViewById(R.id.sectionsRV)
        toDoAdapterInst = toDoAdapter(emptyList(), object : toDoAdapter.OnItemClickListener {
            override fun onItemClick(section: Section) {
                isOpenDetailsVisible = !isOpenDetailsVisible
                toggle(isOpenDetailsVisible, R.id.detailsInclude)
                updateSectionDetails(section)
            }
        })
        sectionsRV.adapter = toDoAdapterInst
        sectionsRV.layoutManager = LinearLayoutManager(requireContext())

        // Sections data
        sectionsViewModel.sections.observe(viewLifecycleOwner) { sections ->
            toDoAdapterInst.updateSections(sections)
        }

        // Details RV
        detailsTasksRV = view.findViewById(R.id.detailsTasksRV)
        detailsTasksAdapter = TasksAdapter(
            onDeleteClick = { /* if deletions are allowed here, call sections VM */ },
            onTitleChanged = { _, _ -> /* handle edits if needed */ }
        )
        detailsTasksRV.adapter = detailsTasksAdapter
        detailsTasksRV.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<ImageButton>(R.id.detailsBack).setOnClickListener {
            toggle(false, R.id.detailsInclude)
        }

        // Add Section action
        addButton.setOnClickListener {
            val title = titleET.text.toString()
            val date = dateET.text.toString()
            val category = categoryET.text.toString()

            if (title.isBlank() || date.isBlank() || category.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tasks = toDoViewModel.tasks.value ?: emptyList()
            val newSection = Section(title, date, category, tasks)
            sectionsViewModel.addSection(newSection)

            titleET.text.clear()
            dateET.text.clear()
            categoryET.text.clear()
            toDoViewModel.clearTasks()

            toggleBottom(false)
            Toast.makeText(requireContext(), "Section added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateSectionDetails(section: Section) {
        view?.findViewById<TextView>(R.id.detailsTitle)?.text = section.title
        view?.findViewById<TextView>(R.id.detailsDate)?.text = section.date
        view?.findViewById<TextView>(R.id.detailsCategory)?.text = section.category
        detailsTasksAdapter.setTasks(section.tasks)
    }

    private fun updateDetails(user: User) {
        val usernameStr = user.username
        val firstInitial = usernameStr.firstOrNull() ?: '-'
        val secondInitial = usernameStr.indexOf(' ')
            .takeIf { it != -1 && it + 1 < usernameStr.length }
            ?.let { usernameStr[it + 1] }

        val initials = buildString {
            append(firstInitial)
            if (secondInitial != null) append(secondInitial)
        }.uppercase()

        imgText.text = initials
        username.text = usernameStr
        email.text = user.email

        usernameET.setText(user.username)
        emailET.setText(user.email)
    }

    private fun darkMode(isDark: Boolean) {
        val loggedInUser = getLoggedInUser(requireContext())
        if (loggedInUser != null) {
            saveUserDarkMode(requireContext(), loggedInUser.username, isDark)
        }
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkmodeIMG.setImageResource(R.drawable.day)
            usernameLogo.setImageResource(R.drawable.white_usernmae)
            emailLogo.setImageResource(R.drawable.white_email)
            passwordLogo.setImageResource(R.drawable.white_password)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkmodeIMG.setImageResource(R.drawable.night)
            usernameLogo.setImageResource(R.drawable.username)
            emailLogo.setImageResource(R.drawable.mail_24dp_000000_fill1_wght400_grad0_opsz24)
            passwordLogo.setImageResource(R.drawable.password)
        }
    }

    private fun toggle(show: Boolean, viewId: Int) {
        val includeLayout = view?.findViewById<View>(viewId) ?: return
        val parent = view?.findViewById<RelativeLayout>(R.id.parent) ?: return
        val transition: Transition = Slide(Gravity.END).apply {
            duration = 200
            addTarget(includeLayout)
        }
        TransitionManager.beginDelayedTransition(parent, transition)
        includeLayout.visibility = if (show) View.VISIBLE else View.GONE
    }

    /** Bottom sheet (add section) */
    private fun toggleBottom(show: Boolean) {
        val includeLayout = view?.findViewById<View>(R.id.addSectionINclude) ?: return
        val parent = view?.findViewById<RelativeLayout>(R.id.parent) ?: return
        val transition: Transition = Slide(Gravity.BOTTOM).apply {
            duration = 200
            addTarget(includeLayout)
        }
        TransitionManager.beginDelayedTransition(parent, transition)
        includeLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
}

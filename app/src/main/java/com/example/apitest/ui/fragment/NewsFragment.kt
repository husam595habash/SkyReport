package com.example.apitest.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apitest.R
import com.example.apitest.ui.activity.MainActivity
import com.example.apitest.ui.adapter.NewsAdapter
import com.example.apitest.ui.viewmodel.NewsViewModel

class NewsFragment : Fragment() {

    private lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewsAdapter
    private lateinit var swipe: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_news, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.RV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]

        val search = view.findViewById<EditText>(R.id.searchEditText)
        search.doOnTextChanged { text, _, _, _ ->
            viewModel.setQuery(text?.toString().orEmpty())
        }


        view.findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            (requireActivity() as? MainActivity)?.toggleFilterOverlay()
        }

        swipe = view.findViewById(R.id.swipeRefresh)
        swipe.setOnRefreshListener {
            viewModel.fetchNews {}
        }

        viewModel.newsList.observe(viewLifecycleOwner) { list ->
            Log.d("NewsFragment", "Received list size: ${list.size}")
            adapter.updateNews(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            swipe.isRefreshing = loading
        }
    }
}


package com.shindejayesharun.saveotask.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shindejayesharun.saveotask.CarouselAdapter
import com.shindejayesharun.saveotask.R
import com.shindejayesharun.saveotask.data.model.SearchResults
import com.shindejayesharun.saveotask.databinding.ActivityHomeBinding
import com.shindejayesharun.saveotask.ui.adapter.CustomAdapterMovies
import com.shindejayesharun.saveotask.ui.moviedetail.MovieDetailScrollingActivity
import com.shindejayesharun.saveotask.util.*
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }

    override val kodein by kodein()
    private lateinit var dataBind: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val factory: HomeViewModelFactory by instance()
    private lateinit var customAdapterMovies: CustomAdapterMovies

    private val itemAdapter by lazy {
        CarouselAdapter { position: Int, item: SearchResults.SearchItem ->
            Toast.makeText(this, "Pos ${position}", Toast.LENGTH_LONG).show()
            item_list.smoothScrollToPosition(position)
        } }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        setupViewModel()
        setupUI()
        initializeObserver()
        handleNetworkChanges()
        setupAPICall()
        viewModel.searchMovie("fast")
        item_list.initialize(itemAdapter)

    }

    private fun setupUI() {
        customAdapterMovies = CustomAdapterMovies()
        dataBind.recyclerViewMovies.apply {
            layoutManager = GridLayoutManager(context,3)
            itemAnimator = DefaultItemAnimator()
            adapter = customAdapterMovies
            addOnItemTouchListener(
                RecyclerItemClickListener(
                    applicationContext,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            if (customAdapterMovies.getData().isNotEmpty()) {
                                val searchItem = customAdapterMovies.getData()[position]
                                searchItem?.let {
                                    val intent =
                                        Intent(
                                            applicationContext,
                                            MovieDetailScrollingActivity::class.java
                                        )
                                    intent.putExtra(AppConstant.INTENT_POSTER, it.poster)
                                    intent.putExtra(AppConstant.INTENT_TITLE, it.title)
                                    startActivity(intent)
                                }

                            }
                        }

                    })
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    viewModel.checkForLoadMoreItems(
                        visibleItemCount,
                        totalItemCount,
                        firstVisibleItemPosition
                    )
                }

            })
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

    }

    private fun initializeObserver() {
        viewModel.movieNameLiveData.observe(this, Observer {
            Log.i("Info", "Movie Name = $it")
        })
        viewModel.loadMoreListLiveData.observe(this, Observer {
            if (it) {
                customAdapterMovies.setData(null)
                Handler().postDelayed({
                    viewModel.loadMore()
                }, 2000)
            }
        })
    }

    private fun setupAPICall() {
        viewModel.moviesLiveData.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    dataBind.recyclerViewMovies.hide()
                    dataBind.progressBar.show()
                }
                is State.Success -> {
                    dataBind.recyclerViewMovies.show()
                    dataBind.progressBar.hide()
                    customAdapterMovies.setData(state.data)
                    itemAdapter.setItems(state.data)
                }
                is State.Error -> {
                    dataBind.progressBar.hide()
                    showToast(state.message)
                }
            }
        })

    }

    private fun handleNetworkChanges() {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, Observer { isConnected ->
            if (!isConnected) {
                dataBind.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                dataBind.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (viewModel.moviesLiveData.value is State.Error || customAdapterMovies.itemCount == 0) {
                    viewModel.getMovies()
                }
                dataBind.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                dataBind.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))

                    animate()
                        .alpha(1f)
                        .setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        })
    }
}


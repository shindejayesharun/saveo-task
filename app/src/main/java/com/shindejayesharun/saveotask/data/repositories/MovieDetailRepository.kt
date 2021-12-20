package com.shindejayesharun.saveotask.data.repositories

import com.shindejayesharun.saveotask.data.model.MovieDetail
import com.shindejayesharun.saveotask.data.network.ApiInterface
import com.shindejayesharun.saveotask.data.network.SafeApiRequest

class MovieDetailRepository(
    private val api: ApiInterface
) : SafeApiRequest() {

    suspend fun getMovieDetail(
        title: String,
        apiKey: String
    ): MovieDetail {

        return apiRequest { api.getMovieDetailData(title, apiKey) }
    }


}
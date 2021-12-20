package com.shindejayesharun.saveotask.data.repositories

import com.shindejayesharun.saveotask.data.model.SearchResults
import com.shindejayesharun.saveotask.data.network.ApiInterface
import com.shindejayesharun.saveotask.data.network.SafeApiRequest

class HomeRepository(
    private val api: ApiInterface
) : SafeApiRequest() {

    suspend fun getMovies(
        searchTitle: String,
        apiKey: String,
        pageIndex: Int
    ): SearchResults {

        return apiRequest { api.getSearchResultData(searchTitle, apiKey, pageIndex) }
    }


}
package ir.parsanasekhi.myapplication.apiManager

import android.util.Log
import ir.parsanasekhi.myapplication.apiManager.model.ChartData
import ir.parsanasekhi.myapplication.apiManager.model.CoinsData
import ir.parsanasekhi.myapplication.apiManager.model.DataNews
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiManager {

    private val apiService: ApiService

    init {

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String, String>>>) {
        apiService.getTopNews().enqueue(object : Callback<DataNews> {
            override fun onResponse(call: Call<DataNews>, response: Response<DataNews>) {
                val dataNews = response.body()!!.data
                val dataToSend = arrayListOf<Pair<String, String>>()
                dataNews.forEach {
                    dataToSend.add(Pair(it.title, it.url))
                }
                apiCallback.onSuccess(dataToSend)
            }

            override fun onFailure(call: Call<DataNews>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })
    }

    fun getCoinsList(apiCallback: ApiCallback<ArrayList<CoinsData.Data>>) {
        apiService.getTopCoins().enqueue(object : Callback<CoinsData> {
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val coinsData = response.body()!!.data
                apiCallback.onSuccess(coinsData as ArrayList<CoinsData.Data>)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })
    }

    fun getDataChart(
        symbol: String,
        period: String,
        apiCallback: ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>>
    ) {

        val histoPeriod: String
        var limit = 30
        var aggregate = 1

        when (period) {

            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }
            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                limit = 6
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }
            else -> {
                histoPeriod = HISTO_DAY
                limit = 2000
                aggregate = 30
            }

        }

        apiService.getChartData(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                    val fullData = response.body()
                    val data1 = fullData!!.data
                    val data2 = fullData.data.maxByOrNull { it.close.toFloat() }
                    apiCallback.onSuccess(Pair(data1, data2))
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)
                }
            })

    }

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(errorMessage: String)
    }

}
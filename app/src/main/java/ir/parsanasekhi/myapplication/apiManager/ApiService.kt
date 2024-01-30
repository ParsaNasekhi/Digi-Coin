package ir.parsanasekhi.myapplication.apiManager

import ir.parsanasekhi.myapplication.apiManager.model.ChartData
import ir.parsanasekhi.myapplication.apiManager.model.CoinsData
import ir.parsanasekhi.myapplication.apiManager.model.DataNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers(API_KEY)
    @GET("v2/news/")
    fun getTopNews(
        @Query("sortOrder") sortOrder: String = "latest"
    ): Call<DataNews>

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getTopCoins(
        @Query("tsym") to_symbol: String = "USD",
        @Query("limit") limit_data: Int = 20
    ): Call<CoinsData>

    @Headers(API_KEY)
    @GET("{period}")
    fun getChartData(
        @Path("period") period: String,
        @Query("fsym") fromSymbol: String,
        @Query("limit") limit: Int,
        @Query("aggregate") aggregate: Int,
        @Query("tsym") toSymbol: String = "USD"
    ): Call<ChartData>

}
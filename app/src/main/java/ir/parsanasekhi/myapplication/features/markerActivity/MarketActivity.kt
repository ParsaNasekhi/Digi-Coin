package ir.parsanasekhi.myapplication.features.markerActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import ir.parsanasekhi.myapplication.apiManager.ApiManager
import ir.parsanasekhi.myapplication.apiManager.model.CoinAboutData
import ir.parsanasekhi.myapplication.apiManager.model.CoinAboutItem
import ir.parsanasekhi.myapplication.apiManager.model.CoinsData
import ir.parsanasekhi.myapplication.databinding.ActivityMarketBinding
import ir.parsanasekhi.myapplication.features.coinActivity.CoinActivity

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {

    private lateinit var binding: ActivityMarketBinding
    val apiManager = ApiManager()
    lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle()
        onMoreClicked()
        onMarketRefreshed()
        getAboutDataFromAssets()

    }

    private fun getAboutDataFromAssets() {
        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val aboutData = gson.fromJson(fileInString, CoinAboutData::class.java)
        aboutDataMap = mutableMapOf<String, CoinAboutItem>()
        aboutData.forEach {
            aboutDataMap[it.currencyName!!] = CoinAboutItem(
                it.info?.web,
                it.info?.github,
                it.info?.twt,
                it.info?.desc,
                it.info?.reddit
            )
        }
    }

    override fun onResume() {
        super.onResume()
        initUi()
    }

    private fun setToolbarTitle() {
        binding.moduleToolbar.toolbar.title = "DigiCoin Market"
    }

    private fun onMarketRefreshed() {
        binding.swipeRefreshMarket.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                initUi()
                binding.swipeRefreshMarket.isRefreshing = false
            }, 1234)
        }
    }

    private fun initUi() {
        getNewsFromApi()
        getCoinsListFromApi()
    }

    private fun onMoreClicked() {
        binding.moduleWatchlist.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com"))
            startActivity(intent)
        }
    }

    private fun getNewsFromApi() {
        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {

            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                refreshNews(data)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "Error:$errorMessage", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    private fun refreshNews(data: ArrayList<Pair<String, String>>) {
        val randomAccess = (0..49).random()
        binding.moduleNews.txtNews.text = data[randomAccess].first
        binding.moduleNews.txtNews.setOnClickListener {
            refreshNews(data)
        }
        binding.moduleNews.imgNews.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data[randomAccess].second))
            startActivity(intent)
        }
    }

    private fun getCoinsListFromApi() {
        apiManager.getCoinsList(object : ApiManager.ApiCallback<ArrayList<CoinsData.Data>> {
            override fun onSuccess(data: ArrayList<CoinsData.Data>) {
                showDataInRecycler(data)
            }

            override fun onError(errorMessage: String) {
                Toast.makeText(this@MarketActivity, "Error: $errorMessage", Toast.LENGTH_SHORT)
                    .show()
                Log.e("TestLog", "onError: $errorMessage")
            }

        })
    }

    private fun showDataInRecycler(data: ArrayList<CoinsData.Data>) {
        binding.moduleWatchlist.recyclerWatchlist.layoutManager =
            LinearLayoutManager(this@MarketActivity, RecyclerView.VERTICAL, false)
        binding.moduleWatchlist.recyclerWatchlist.adapter = MarketAdapter(data, this)
    }

    override fun onCoinItemClicked(data: CoinsData.Data) {
        val intent = Intent(this, CoinActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("bundle1", data)
        bundle.putParcelable("bundle2", aboutDataMap[data.coinInfo.name])
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }
}
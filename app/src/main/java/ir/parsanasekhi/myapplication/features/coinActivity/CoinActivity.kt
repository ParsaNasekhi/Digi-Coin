package ir.parsanasekhi.myapplication.features.coinActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ir.parsanasekhi.myapplication.R
import ir.parsanasekhi.myapplication.apiManager.*
import ir.parsanasekhi.myapplication.apiManager.model.ChartData
import ir.parsanasekhi.myapplication.apiManager.model.CoinAboutItem
import ir.parsanasekhi.myapplication.apiManager.model.CoinsData
import ir.parsanasekhi.myapplication.databinding.ActivityCoinBinding

const val BASE_URL_TWITTER = "https://twitter.com/"

class CoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCoinBinding
    private lateinit var thisCoinData: CoinsData.Data
    private lateinit var thisCoinAbout: CoinAboutItem
    val apiManager = ApiManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCoinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        receiveThisCoinData()

        setToolbarTitle()
        initUi()

    }

    private fun receiveThisCoinData() {
        val bundle = intent.getParcelableExtra<Bundle>("bundle")!!
        thisCoinData = bundle.getParcelable<CoinsData.Data>("bundle1")!!
        if (bundle.getParcelable<CoinAboutItem>("bundle2") != null) {
            thisCoinAbout = bundle.getParcelable<CoinAboutItem>("bundle2")!!
        } else {
            thisCoinAbout = CoinAboutItem()
        }
    }

    private fun setToolbarTitle() {
        binding.moduleToolbar.toolbar.title = thisCoinData.coinInfo.fullName
    }

    private fun initUi() {
        initStatistics()
        initAbout()
        initChart()
    }

    private fun initChart() {

        var period = HOUR
        requestAndShowChartData(period)

        binding.moduleChart.radioGroupChart.setOnCheckedChangeListener { _, i ->
            period = when (i) {
                R.id.radio_12h -> {
                    HOUR
                }
                R.id.radio_1d -> {
                    HOURS24
                }
                R.id.radio_1w -> {
                    WEEK
                }
                R.id.radio_1m -> {
                    MONTH
                }
                R.id.radio_3m -> {
                    MONTH3
                }
                R.id.radio_1y -> {
                    YEAR
                }
                else -> {
                    ALL
                }
            }
            requestAndShowChartData(period)
        }

        binding.moduleChart.txtChartPrice.text = thisCoinData.dISPLAY.uSD.pRICE
        binding.moduleChart.txtChartChange1.text = " " + thisCoinData.dISPLAY.uSD.cHANGE24HOUR
        var change = thisCoinData.rAW.uSD.cHANGEPCT24HOUR.toString() + "00"
        val dotIndexOfChange = change.indexOf('.')
        change = change.substring(0, dotIndexOfChange + 3)
        if (change.toDouble() < 0) {
            binding.moduleChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.colorLoss
                )
            )
            binding.moduleChart.txtChartChange2.text = change + "%"
            binding.moduleChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.colorLoss
                )
            )
            binding.moduleChart.txtChartUpdown.text = "▼"
            binding.moduleChart.sparkViewMain.lineColor = ContextCompat.getColor(
                binding.root.context, R.color.colorLoss
            )
        } else if (change.toDouble() > 0) {
            binding.moduleChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.colorGain
                )
            )
            binding.moduleChart.txtChartChange2.text = "+" + change + "%"
            binding.moduleChart.txtChartUpdown.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.colorGain
                )
            )
            binding.moduleChart.txtChartUpdown.text = "▲"
            binding.moduleChart.sparkViewMain.lineColor = ContextCompat.getColor(
                binding.root.context, R.color.colorGain
            )
        } else {
            binding.moduleChart.txtChartChange2.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.tertiaryTextColor
                )
            )
            binding.moduleChart.txtChartChange2.text = change + "%"
            binding.moduleChart.txtChartUpdown.text = ""
            binding.moduleChart.sparkViewMain.lineColor = ContextCompat.getColor(
                binding.root.context, R.color.tertiaryTextColor
            )
        }

        binding.moduleChart.sparkViewMain.setScrubListener {
            if (it == null) {
                binding.moduleChart.txtChartPrice.text = thisCoinData.dISPLAY.uSD.pRICE
            } else {
                binding.moduleChart.txtChartPrice.text = "$" + (it as ChartData.Data).close
            }
        }
    }

    private fun requestAndShowChartData(period: String) {
        apiManager.getDataChart(thisCoinData.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data>, ChartData.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data>, ChartData.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.moduleChart.sparkViewMain.adapter = chartAdapter
                    Log.e("TestLog", "onError: ${data.first}")
                }

                override fun onError(errorMessage: String) {
                    Toast.makeText(this@CoinActivity, "Error: $errorMessage", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("TestLog", "onError: $errorMessage")
                }

            })
    }

    private fun initAbout() {
        binding.moduleAbout.txtWebsite.text = thisCoinAbout.coinWebsite
        binding.moduleAbout.txtGithub.text = thisCoinAbout.coinGithub
        binding.moduleAbout.txtReddit.text = thisCoinAbout.coinReddit
        if (thisCoinAbout != CoinAboutItem()) {
            binding.moduleAbout.txtTwitter.text = "@" + thisCoinAbout.coinTwitter
        } else {
            binding.moduleAbout.txtTwitter.text = thisCoinAbout.coinTwitter
        }
        binding.moduleAbout.txtAboutCoin.text = thisCoinAbout.coinDesc

        binding.moduleAbout.txtWebsite.setOnClickListener {
            openWebsiteDataCoin(thisCoinAbout.coinWebsite!!)
        }
        binding.moduleAbout.txtGithub.setOnClickListener {
            openWebsiteDataCoin(thisCoinAbout.coinGithub!!)
        }
        binding.moduleAbout.txtReddit.setOnClickListener {
            openWebsiteDataCoin(thisCoinAbout.coinReddit!!)
        }
        binding.moduleAbout.txtTwitter.setOnClickListener {
            openWebsiteDataCoin(BASE_URL_TWITTER + thisCoinAbout.coinWebsite!!)
        }

    }

    private fun openWebsiteDataCoin(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun initStatistics() {

        binding.moduleStatistics.tvOpenAmount.text = thisCoinData.dISPLAY.uSD.oPEN24HOUR
        binding.moduleStatistics.tvTodaysHighAmount.text = thisCoinData.dISPLAY.uSD.hIGH24HOUR
        binding.moduleStatistics.tvTodayLowAmount.text = thisCoinData.dISPLAY.uSD.lOW24HOUR
        binding.moduleStatistics.tvChangeTodayAmount.text = thisCoinData.dISPLAY.uSD.cHANGE24HOUR
        binding.moduleStatistics.tvAlgorithmAmount.text = thisCoinData.coinInfo.algorithm
        binding.moduleStatistics.tvTotalVolumeAmount.text = thisCoinData.dISPLAY.uSD.tOTALVOLUME24H
        binding.moduleStatistics.tvAvgMarketCapAmount.text = thisCoinData.dISPLAY.uSD.mKTCAP
        binding.moduleStatistics.tvSupplyNumber.text = thisCoinData.dISPLAY.uSD.sUPPLY

    }
}
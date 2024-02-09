package ir.parsanasekhi.myapplication.features.markerActivity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import ir.parsanasekhi.myapplication.R
import ir.parsanasekhi.myapplication.apiManager.BASE_URL_IMAGE
import ir.parsanasekhi.myapplication.apiManager.model.CoinsData
import ir.parsanasekhi.myapplication.databinding.ItemRecyclerWatchlistBinding

class MarketAdapter(
    private val data: ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    lateinit var binding: ItemRecyclerWatchlistBinding

    inner class MarketViewHolder(itemView: View) : ViewHolder(itemView) {


        fun bindViews(coinData: CoinsData.Data) {

            try {

                binding.txtCoinName.text = coinData.coinInfo.fullName
                binding.txtPrice.text = "$" + coinData.rAW.uSD.pRICE.toString().substring(
                    0,
                    coinData.rAW.uSD.pRICE.toString().indexOf('.') + 3
                )

                showMarketCap(coinData)
                showChange(coinData)
                showImage(coinData)
                onItemClicked(coinData)

            } catch (e: Exception) { }

        }

        private fun onItemClicked(coinData: CoinsData.Data) {
            binding.itemWatchlist.setOnClickListener {
                recyclerCallback.onCoinItemClicked(coinData)
            }
        }

        private fun showImage(coinData: CoinsData.Data) {
            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + coinData.coinInfo.imageUrl)
                .into(binding.imgItem)
        }

        private fun showChange(coinData: CoinsData.Data) {
            var change = coinData.rAW.uSD.cHANGEPCT24HOUR.toString() + "00"
            val dotIndexOfChange = change.indexOf('.')
            change = change.substring(0, dotIndexOfChange + 3)
            if (change.toDouble() < 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorLoss
                    )
                )
                binding.txtChange.text = change + "%"
            } else if (change.toDouble() > 0) {
                binding.txtChange.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGain
                    )
                )
                binding.txtChange.text = "+" + change + "%"
            } else binding.txtChange.text = change + "%"
        }

        private fun showMarketCap(coinData: CoinsData.Data) {
            var marketCap = (coinData.rAW.uSD.mKTCAP / 1000000000).toString() + "00"
            val dotIndexOfMarketCap = marketCap.indexOf('.')
            marketCap = marketCap.substring(0, dotIndexOfMarketCap + 3)
            binding.txtMarketCap.text = "$" + marketCap + "B"
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        binding =
            ItemRecyclerWatchlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarketViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    interface RecyclerCallback {
        fun onCoinItemClicked(data: CoinsData.Data)
    }

}
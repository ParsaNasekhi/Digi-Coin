package ir.parsanasekhi.myapplication.apiManager.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem(
    var coinWebsite: String? = "N/A",
    var coinGithub: String? = "N/A",
    var coinTwitter: String? = "N/A",
    var coinDesc: String? = "N/A",
    var coinReddit: String? = "N/A"
) : Parcelable
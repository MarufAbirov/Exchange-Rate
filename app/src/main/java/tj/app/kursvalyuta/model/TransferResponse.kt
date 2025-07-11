package tj.app.kursvalyuta.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankData(
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("ShortName") val shortName: String,
    val colors: BankColors,
    val icon: String,
    @SerializedName("is_change") val isChange: Boolean,
    @SerializedName("android_link") val androidLink: String,
    @SerializedName("ios_link") val iosLink: String,
    @SerializedName("Currency") val currencyList: List<Currency>
) : Parcelable

@Parcelize
data class BankColors(
    @SerializedName("color_1") val color1: String,
    @SerializedName("color_2") val color2: String
) : Parcelable

@Parcelize
data class Currency(
    val name: String?,
    @SerializedName("full_name") val fullName: String,
    val flag: String,
    @SerializedName("sell_value") val sellValue: String,
    @SerializedName("buy_value") val buyValue: String
) : Parcelable

package tj.app.kursvalyuta.retrofit

import retrofit2.http.GET
import tj.app.kursvalyuta.model.BankData

interface ApiService {

    @GET("currency-app/v1/npcr_bank_rates")
    suspend fun getBankList(): List<BankData>
}
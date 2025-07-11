package tj.app.kursvalyuta

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import tj.app.kursvalyuta.databinding.FragmentNbtBankBinding
import tj.app.kursvalyuta.model.CurrencyRate
import tj.app.kursvalyuta.retrofit.RetrofitInstance

class NbtBankFragment : Fragment() {

    private var _binding: FragmentNbtBankBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNbtBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            try {
                val allBanks = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getBankList()
                }

                val humoBank = allBanks.find { it.shortName.equals("humo", ignoreCase = true) }
                binding.converNBT.setOnClickListener {
                    CurrencyConverterBottomSheet(humoBank!!).show(parentFragmentManager, "converter_sheet")
                }
                if (humoBank != null) {
                    val usd = humoBank.currencyList.find { it.name == "USD" }
                    val eur = humoBank.currencyList.find { it.name == "EUR" }
                    val rub = humoBank.currencyList.find { it.name == "RUB" }

                    binding.nbtUsd.text = usd?.sellValue ?: "-"
                    binding.nbtEur.text = eur?.sellValue ?: "-"
                    binding.nbtRub.text = rub?.sellValue ?: "-"
                } else {
                    Log.e("NbtBankFragment", "Humo ёфт нашуд")
                }

            } catch (e: Exception) {
                Log.e("NbtBankFragment", "Хато: ${e.message}")
            }
        }

    }

}
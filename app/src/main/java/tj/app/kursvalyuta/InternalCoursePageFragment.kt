package tj.app.kursvalyuta

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import tj.app.kursvalyuta.databinding.FragmentInternalCoursePageBinding


class InternalCoursePageFragment : Fragment() {
    private var _binding: FragmentInternalCoursePageBinding? = null
    private val binding get() = _binding!!
    private val args: InternalCoursePageFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInternalCoursePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bank = args.bankData
        Glide.with(this).load(bank.icon).into(binding.iconPageBank)

        val rub = bank.currencyList.find { it.name == "RUB" }
        val usd = bank.currencyList.find { it.name == "USD" }
        val eur = bank.currencyList.find { it.name == "EUR" }

        binding.sellValueRub.text = rub?.sellValue ?: "-"
        binding.sellValueUsd.text = usd?.sellValue ?: "-"
        binding.sellValueEur.text = eur?.sellValue ?: "-"

        binding.byValueRub.text = rub?.buyValue ?: "-"
        binding.byValueUsd.text = usd?.buyValue ?: "-"
        binding.byValueEur.text = eur?.buyValue ?: "-"

        binding.shareBanki.setOnClickListener {
            val sharingintent = Intent(Intent.ACTION_SEND)
            sharingintent.type = "text/plain"
            sharingintent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingintent.putExtra(
                Intent.EXTRA_TEXT,
                "Курс валюта на ${bank.bankName}\n" +
                        "Покупка: 1 RUB ${rub?.buyValue} Продажа: 1RUB ${rub?.sellValue}\n" +
                        "Покупка: 1 USD ${usd?.buyValue} Продажа: 1 USD ${usd?.sellValue}\n" +
                        "Покупка: 1 EUR ${eur?.buyValue} Продажа: 1 EUR ${eur?.sellValue}\n"
            )
            startActivity(Intent.createChooser(sharingintent, "Поделиться"))

        }

        val color1Hex = bank.colors.color1
        val color2Hex = bank.colors.color2

        requireActivity().window.statusBarColor = Color.parseColor(color1Hex)
        requireActivity().window.decorView.systemUiVisibility = 0

        requireActivity().window.insetsController?.setSystemBarsAppearance(
            0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        val gradient = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor(color1Hex), Color.parseColor(color2Hex))
        )
        binding.root.background = gradient

        binding.btnBackInterPage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnConversion.setOnClickListener {
            CurrencyConverterBottomSheet(bank).show(parentFragmentManager, "converter_sheet")
        }
    }



    }
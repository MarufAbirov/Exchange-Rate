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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.parcelableCreator
import tj.app.kursvalyuta.adapter.BankAdapter
import tj.app.kursvalyuta.databinding.FragmentFavorableRateBinding
import tj.app.kursvalyuta.model.BankData
import tj.app.kursvalyuta.retrofit.RetrofitInstance

class FavorableRateFragment : Fragment() {
    private var _binding: FragmentFavorableRateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavorableRateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bank = arguments?.getParcelable<BankData>("humoBank")

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#DE5000"),
                Color.parseColor("#FC8D26")
            )
        )
        gradientDrawable.cornerRadius = 28f

        binding.cardhumo.background = gradientDrawable

        binding.shareKurs.setOnClickListener {
            val sharingintent = Intent(Intent.ACTION_SEND)
            sharingintent.type = "text/plain"
            sharingintent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            sharingintent.putExtra(
                Intent.EXTRA_TEXT,
                "1000 RUB = ${binding.kursRubNumoConverter.text}"
            )
            startActivity(Intent.createChooser(sharingintent, "Поделиться"))

        }

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.fav_rate_page)
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        val collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsingToolbarCollapsed)
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbarExpanded)
        collapsingToolbar.title = "Выгодный курс"

        val adapter = BankAdapter(requireContext()) { bankData ->
            val action = FavorableRateFragmentDirections.actionFavorableRateFragmentToInternalCoursePage(bankData)
            findNavController().navigate(action)
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            try {
                val allBanks = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getBankList()
                }

                val humoBank = allBanks.find { it.shortName.equals("humo", ignoreCase = true) }
                binding.converterKurs.setOnClickListener {
                    CurrencyConverterBottomSheet(humoBank!!).show(parentFragmentManager, "converter_sheet")
                }
                if (humoBank != null) {
                    val rub = humoBank.currencyList.find { it.name == "RUB" }
                    binding.kursRubNumoConverter.text = (rub?.sellValue?.toDoubleOrNull()
                        ?.times(1000)).toString() ?: ""
                }

                    val response = RetrofitInstance.api.getBankList()
                adapter.submitList(response)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Хато: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
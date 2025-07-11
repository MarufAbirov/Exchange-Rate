package tj.app.kursvalyuta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tj.app.kursvalyuta.databinding.SheetConversionSelectionBinding

class CurrencySelectorBottomSheet(
    onCurrencySelected1: String,
    private val currentText: String,
    private val onCurrencySelected: (String) -> Unit,
) : BottomSheetDialogFragment() {

    private var _binding: SheetConversionSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =SheetConversionSelectionBinding.inflate(inflater, container, false)

        if (currentText=="EUR") binding.eur.visibility = View.GONE
        if (currentText=="RUB") binding.rubl.visibility = View.GONE
        if (currentText=="USD") binding.usd.visibility = View.GONE
        if (currentText=="TJS") binding.somoni.visibility = View.GONE
        binding.eur.setOnClickListener {
            onCurrencySelected("EUR")
            dismiss()
        }

        binding.usd.setOnClickListener {
            onCurrencySelected("USD")
            dismiss()
        }

        binding.rubl.setOnClickListener {
            onCurrencySelected("RUB")
            dismiss()
        }
        binding.somoni.setOnClickListener {
            onCurrencySelected("TJS")
            dismiss()
        }

        return binding.root
    }
}
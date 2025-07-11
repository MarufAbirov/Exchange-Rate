package tj.app.kursvalyuta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tj.app.kursvalyuta.databinding.BottomSheetConversionBinding
import tj.app.kursvalyuta.model.BankData
import java.text.DecimalFormat

class CurrencyConverterBottomSheet(private val bankData: BankData) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetConversionBinding? = null
    private val binding get() = _binding!!
    private val format = DecimalFormat("0.00")
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetConversionBinding.inflate(inflater, container, false)

        setupCurrencySelectors()
        setupSwapButton()
        setupConversionFields()

        binding.clearBottom.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupCurrencySelectors() {
        val rub = bankData.currencyList.find { it.name.equals("RUB", ignoreCase = true) }
        binding.converRubl.text = "1 RUB = ${rub?.sellValue ?: "-"}"

        binding.selectKurs1.setOnClickListener {
            val currentValue = binding.kurs1.text.toString()
            CurrencySelectorBottomSheet("kurs1", currentValue) { selected ->
                binding.kurs1.text = selected
                triggerRecalculationFromEditText1()
            }.show(parentFragmentManager, "currency_sheet")
        }

        binding.selectKurs2.setOnClickListener {
            val currentValue = binding.kurs2.text.toString()
            CurrencySelectorBottomSheet("kurs2",currentValue) { selected ->
                binding.kurs2.text = selected
                triggerRecalculationFromEditText1()
            }.show(parentFragmentManager, "currency_sheet")
        }
    }

    private fun setupSwapButton() {
        binding.swapValyuta.setOnClickListener {
            val temp = binding.kurs1.text
            binding.kurs1.text = binding.kurs2.text
            binding.kurs2.text = temp

//            val tempText = binding.editText1.text
//            binding.editText1.text = binding.editText2.text
//            binding.editText2.text = tempText
                triggerRecalculationFromEditText1()
        }
    }

    private fun setupConversionFields() {
        binding.editText1.setText("")
        binding.editText2.setText("")

        binding.editText1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isEditing) {
                    isEditing = true
                    val amount = s.toString().toDoubleOrNull()
                    if (amount != null) {
                        val from = binding.kurs1.text.toString()
                        val to = binding.kurs2.text.toString()
                        val result = convert(from, to, amount)
                        binding.editText2.setText(format.format(result))
                    } else {
                        binding.editText2.setText("")
                    }
                    isEditing = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.editText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isEditing) {
                    isEditing = true
                    val amount = s.toString().toDoubleOrNull()
                    if (amount != null) {
                        val from = binding.kurs2.text.toString()
                        val to = binding.kurs1.text.toString()
                        val result = convert(from, to, amount)
                        binding.editText1.setText(format.format(result))
                    } else {
                        binding.editText1.setText("")
                    }
                    isEditing = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun triggerRecalculationFromEditText1() {
        val from = binding.kurs1.text.toString()
        val to = binding.kurs2.text.toString()
        val amount = binding.editText1.text.toString().toDoubleOrNull()
        if (amount != null) {
            val result = convert(from, to, amount)
            binding.editText2.setText(format.format(result))
        } else {
            binding.editText2.setText("")
        }
    }

    private fun convert(from: String, to: String, amount: Double): Double {
        val fromRate = getRate(from)
        val toRate = getRate(to)

        return when {
            from == "TJS" && to != "TJS" -> amount / toRate
            from != "TJS" && to == "TJS" -> amount * fromRate
            from != "TJS" && to != "TJS" -> {
                val tjs = amount * fromRate
                tjs / toRate
            }
            else -> amount
        }
    }

    private fun getRate(currencyName: String): Double {
        return bankData.currencyList
            .find { it.name.equals(currencyName, ignoreCase = true) }
            ?.sellValue
            ?.toDoubleOrNull() ?: 1.0
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            it.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_bottom_sheet)
            BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
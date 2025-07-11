package tj.app.kursvalyuta.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tj.app.kursvalyuta.databinding.ItemBankBinding
import tj.app.kursvalyuta.model.BankData
import tj.app.kursvalyuta.model.Currency

class BankAdapter(private val context: Context,
                  private val onItemClick: (BankData) -> Unit
) : RecyclerView.Adapter<BankAdapter.MyViewHolder>() {

    private var bankList: List<BankData> = emptyList()

    inner class MyViewHolder(private val binding: ItemBankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bank: BankData) {
            Glide.with(context).load(bank.icon).into(binding.bankIcon)
            binding.nameBank.text = bank.bankName

            val currency = bank.currencyList.find { it.name == "RUB" }
            binding.setValue.text = currency?.sellValue ?: "-"
            binding.buyValue.text = currency?.buyValue ?: "-"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemBankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = bankList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = bankList.size

    fun submitList(list: List<BankData>) {
        bankList = list
        notifyDataSetChanged()
    }
}
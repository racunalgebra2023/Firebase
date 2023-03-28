package hr.algebra.firebase.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.firebase.databinding.ItemBinding
import hr.algebra.firebase.model.Todo

interface OnItemClickListener {
    fun onClick( item : Todo )
}

class TodosAdapter( private val list: List< Todo >, val listener : OnItemClickListener ) : RecyclerView.Adapter< TodosAdapter.ViewHolder >( ) {

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): ViewHolder {
        val view = ItemBinding.inflate( LayoutInflater.from( parent.context ) , parent, false )
        return ViewHolder( view )
    }

    override fun onBindViewHolder( holder: ViewHolder, position: Int ) {
        holder.bind( list[position] )
    }

    override fun getItemCount( ) = list.size

    inner class ViewHolder( binding : ItemBinding ) : RecyclerView.ViewHolder( binding.root ) {
        val tvTitle = binding.tvTitle
        val tvDesc  = binding.tvDesc

        fun bind( item : Todo ) {
            tvTitle.text = item.name
            tvDesc.text  = item.desc
            itemView.setOnClickListener { listener.onClick( item ) }
        }
    }
}
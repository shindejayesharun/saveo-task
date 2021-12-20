package com.shindejayesharun.saveotask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shindejayesharun.saveotask.data.model.SearchResults
import kotlinx.android.synthetic.main.carousel_item.view.*



class CarouselAdapter(val itemClick: (Int, SearchResults.SearchItem) -> Unit) : RecyclerView.Adapter<ItemViewHolder>() {

    private var items: List<SearchResults.SearchItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.carousel_item, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(holder.itemView.context,items[position])
        holder.itemView.setOnClickListener {
            itemClick(position,items[position])
        }
    }

    override fun getItemCount() = items.size

    fun setItems(newItems: ArrayList<SearchResults.SearchItem?>) {
        if(newItems.isNotEmpty()) {
            items = newItems as ArrayList<SearchResults.SearchItem>;
            notifyDataSetChanged()
        }
    }
}

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(context: Context, item: SearchResults.SearchItem) {
        Glide.with(context).load(item.poster)
            .centerCrop()
            .thumbnail(0.5f)
            .placeholder(R.drawable.ic_launcher_background)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(view.list_item_icon)
    }
}
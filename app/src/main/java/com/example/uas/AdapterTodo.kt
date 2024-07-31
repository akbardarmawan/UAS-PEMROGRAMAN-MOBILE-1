package com.example.uas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AdapterTodo (private val itemList: List<ItemList>) : RecyclerView.Adapter<AdapterTodo.ViewHolder> () {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(
            item: ItemList
        )
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgView : ImageView = itemView.findViewById(R.id.titleImg)
        val title : TextView = itemView.findViewById(R.id.title)
        val desc : TextView = itemView.findViewById(R.id.subTitle)
    }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): AdapterTodo.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterTodo.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.title.text = item.title
        holder.desc.text = item.desc
        Glide.with(holder.imgView.context).load(item.image).into(holder.imgView)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
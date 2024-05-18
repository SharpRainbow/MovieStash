package ru.mirea.moviestash.collections

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mirea.moviestash.R
import ru.mirea.moviestash.entites.Collection
import java.util.Random


class CollectionAdapter(
    private val cols: List<Collection>,
    private val home: Boolean,
    private val clickListener: (elem: Collection) -> Unit
) : RecyclerView.Adapter<CollectionAdapter.ColViewHolder>() {

    private lateinit var context: Context
    private var r: Random = Random()
    private var startColors = intArrayOf(
        Color.parseColor("#f7c0ec"),
        Color.parseColor("#b9dcf2"),
        Color.parseColor("#d3f3f1"),
        Color.parseColor("#9bf8f4"),
        Color.parseColor("#f9c58d"),
        Color.parseColor("#ebf4f5")
    )
    private var endColors = intArrayOf(
        Color.parseColor("#a7bdea"),
        Color.parseColor("#f6cfbe"),
        Color.parseColor("#e9b7ce"),
        Color.parseColor("#6f7bf7"),
        Color.parseColor("#f492f0"),
        Color.parseColor("#b5c6e0")
    )

    class ColViewHolder(colView: View) : RecyclerView.ViewHolder(colView) {
        val name: TextView = itemView.findViewById(R.id.colName)
        val layout: LinearLayout = itemView.findViewById(R.id.colLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_col, parent, false)
        return ColViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ColViewHolder, position: Int) {
        holder.name.text = cols[position].name
        if (!home) {
            holder.itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            holder.name.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val sel = r.nextInt(startColors.size)
        val drawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(startColors[sel], endColors[sel])
        )
        drawable.cornerRadius = 20f
        holder.layout.background = drawable
        holder.itemView.setOnClickListener {
            val intent = Intent(context, CollectionActivity::class.java)
            intent.putExtra("COL", cols[position])
            context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener {
            clickListener(cols[position])
            true
        }
    }

    override fun getItemCount(): Int {
        return cols.size
    }
}
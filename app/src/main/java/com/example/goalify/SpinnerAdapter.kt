package com.example.goalifyperso

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.goalify.R

class SpinnerAdapter(
    context: Context,
    private val images: List<SpinnerItem>
) : ArrayAdapter<SpinnerItem>(context, 0, images) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_color, parent, false)
        val item = images[position]

        val imageView = view.findViewById<ImageView>(R.id.image_view)
        imageView.setImageResource(item.imageResId)
        imageView.tag = item.text
        return view
    }
}
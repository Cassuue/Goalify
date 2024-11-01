package ca.uqac.goalify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

class SpinnerAdapterColor(
    context: Context,
    private val images: List<SpinnerItemColor>
) : ArrayAdapter<SpinnerItemColor>(context, 0, images) {

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
package ca.uqac.goalify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ca.uqac.goalify.ca.uqac.goalify.Reward
import com.bumptech.glide.Glide

class RewardAdapter(
    private val context: Context,
    private var rewards: List<Reward>
) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    fun updateData(newRewards: List<Reward>) {
        rewards = newRewards
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.title.text = reward.title
        holder.subtitle.text = reward.subtitle

        if (reward.isUnlocked) {
            Glide.with(context).load(reward.imageUrl).into(holder.image)
            holder.title.setTextColor(context.getColor(android.R.color.black))
            holder.subtitle.setTextColor(context.getColor(android.R.color.black))
        } else {
            Glide.with(context).load(reward.imageUrl).into(holder.image)
            holder.image.setColorFilter(context.getColor(android.R.color.darker_gray))
            holder.title.setTextColor(context.getColor(android.R.color.darker_gray))
            holder.subtitle.setTextColor(context.getColor(android.R.color.darker_gray))
        }
    }

    override fun getItemCount(): Int = rewards.size

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.achievement_image)
        val title: TextView = itemView.findViewById(R.id.Reward_title)
        val subtitle: TextView = itemView.findViewById(R.id.Reward_subtitle)
    }
}

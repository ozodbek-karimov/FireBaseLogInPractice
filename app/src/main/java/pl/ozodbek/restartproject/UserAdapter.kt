package pl.ozodbek.restartproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.squareup.picasso.Picasso
import pl.ozodbek.restartproject.databinding.UserRowBinding
import pl.ozodbek.restartproject.models.Users

class UserAdapter(
    val list: List<Users>,
    val itemTouchListener: (user:Users) -> Unit
) : RecyclerView.Adapter<UserAdapter.Vh>() {

    inner class Vh(private val userRowBinding: UserRowBinding) :
        RecyclerView.ViewHolder(userRowBinding.root) {
        fun onBind(user: Users) {
            userRowBinding.apply {
                Picasso.get().load(user.photoUrl).into(userImageView)
                userName.text = user.displayName
                userEmail.text = user.email

                card.setOnClickListener {
                    itemTouchListener.invoke(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

}
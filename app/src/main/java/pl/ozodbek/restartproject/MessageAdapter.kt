package pl.ozodbek.restartproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.ozodbek.restartproject.databinding.UserFromRowBinding
import pl.ozodbek.restartproject.databinding.UserToRowBinding
import pl.ozodbek.restartproject.models.Message

class MessageAdapter(val list: List<Message>, val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TO = 0
    val FROM = 1

    inner class toVh(val userToRowBinding: UserToRowBinding) :
        RecyclerView.ViewHolder(userToRowBinding.root){
            fun onBind(message: Message){
                userToRowBinding.apply {
                    messageTextview.text = message.text
                    dataTextview.text = message.date
                }
            }
        }


    inner class fromVh(val userFromRowBinding: UserFromRowBinding) :
        RecyclerView.ViewHolder(userFromRowBinding.root){
        fun onBind(message: Message){
            userFromRowBinding.apply {
                messageTextview.text = message.text
                dataTextview.text = message.date
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return toVh(
                UserToRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return fromVh(
                UserFromRowBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//        if (holder is toVh){
//            holder.onBind(list[position])
//        }else if(holder is fromVh){
//            holder.onBind(list[position])
//        }
        when(holder){
            is toVh -> holder.onBind(list[position])
            is fromVh -> holder.onBind(list[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUserId == currentUserId) {
            return FROM
        }
        return TO
    }

}
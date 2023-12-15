package pl.ozodbek.restartproject.models

class Message {

    var text:String? = null
    var toUserId:String? = null
    var fromUserId:String? = null
    var date:String? = null

    constructor(text: String?, toUserId: String?, fromUserId: String?, date: String?) {
        this.text = text
        this.toUserId = toUserId
        this.fromUserId = fromUserId
        this.date = date
    }

    constructor()


    override fun toString(): String {
        return "Message(text=$text, toUserId=$toUserId, fromUserId=$fromUserId, date=$date)"
    }
}
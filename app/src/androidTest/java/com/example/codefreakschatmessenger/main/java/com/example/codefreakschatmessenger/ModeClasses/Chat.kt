package com.example.codefreakschatmessenger.ModeClasses

class Chat
{
    private var sender:String=""
    private var message:String=""
    private var receiver:String=""
    private var isseen=false
    private var url:String=""
    private var messageId:String=""


    constructor()
    constructor(
        sender: String,
        message: String,
        receiver: String,
        isseen: Boolean,
        url: String,
        messageId: String
    ) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isseen = isseen
        this.url = url
        this.messageId = messageId
    }

    //sender
    fun getSender():String?
    {
        return sender
    }
    fun setSender(sender: String)
    {
        this.sender=sender!!
    }


    //message
    fun getMessage():String?
    {
        return message
    }
    fun setMessage(message: String)
    {
        this.message=message!!
    }

//reciver



    fun getReceiver():String?
    {
        return receiver
    }
    fun setReceiver(receiver: String)
    {
        this.receiver=receiver
    }



//is seen


    fun getIsseen():Boolean
    {
        return isseen
    }
    fun setIsseen(isseen: Boolean)
    {
        this.isseen=isseen
    }




//url


    fun getUrl():String?
    {
        return url
    }
    fun setUrl(url: String)
    {
        this.url=url
    }



//message Id


    fun getMessageID():String?
    {
        return messageId
    }
    fun setMessageID(messageId: String)
    {
        this.messageId=messageId
    }



}
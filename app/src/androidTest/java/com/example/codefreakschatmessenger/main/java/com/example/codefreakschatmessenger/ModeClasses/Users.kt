package com.example.codefreakschatmessenger.ModeClasses

class Users
{
    private var uid:String=""
    private var username:String=""
    private var profile:String=""
    private var cover:String=""
    private var status:String=""
    private var search:String=""
    private var facebook:String=""
    private var instagram:String=""
    private var website:String=""

    constructor() //Empty Contructor


    constructor(   //Contructor with all parameteres
        uid: String,
        username: String,
        profile: String,
        cover: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String
    ) {
        this.uid = uid
        this.username = username
        this.profile = profile
        this.cover = cover
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
    }

    //Adding setters and getters for all the fields

    fun getUid():String?
    {
        return uid
    }
    fun setUid(uid: String)
    {
        this.uid=uid

    }

    fun getUsername():String?
    {
     return username
    }
    fun setUsername(username: String)
    {
        this.username=username

    }

    fun getProfile():String?
    {
        return profile
    }
    fun setProfile(profile: String)
    {
        this.profile=profile

    }

    fun getCover():String?
    {
        return cover
    }
    fun setCover(cover: String)
    {
        this.cover=cover

    }

    fun getStatus():String?
    {
        return status
    }
    fun setStatus(status: String)
    {
        this.status=status

    }

    fun getSerach():String?
    {
        return search
    }
    fun setSerach(search: String)
    {
        this.search=search

    }

    fun getFacebook():String?
    {
        return facebook
    }
    fun setFacebook(facebook: String)
    {
        this.facebook=facebook

    }

    fun getInstagram():String?
    {
        return instagram
    }
    fun setInstagram(instagram: String)
    {
        this.instagram=instagram
    }

    fun getWebsite():String?
    {
        return website
    }
    fun setWebsite(website: String)
    {
        this.website=website

    }





}
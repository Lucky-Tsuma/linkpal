package com.lucky.fundiapp

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

/*ViewModel objects used to serve data to UI controllers(Fragments and Activities). They survive configuration changes so in my case I used them
* to retain Image and text so they will not be lost during configuration change*/

class MyViewModel : ViewModel(){
    private var image : Drawable? = null
    private var someText : String? = null
    private var someNumber : Int? = null


    fun getImage(): Drawable? = image

    fun setImage(image: Drawable) {
        this.image = image
    }

    fun getText() : String? = someText

    fun setText(someText: String) {
        this.someText = someText
    }

    fun getNumber() : Int? = someNumber

    fun setNumber(someText: Int) {
        this.someNumber = someNumber
    }
}
package com.lucky.fundiapp

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class WorkerSignup2ViewModel : ViewModel() {
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
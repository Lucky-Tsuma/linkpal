package com.lucky.fundiapp

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

/*ViewModel objects used to serve data to UI controllers(Fragments and Activities). In my case here, I use the PicViewModel class to create
* ViewModel objects to retain an image and send it back to a UI component in case of configuration changes since, for memory intensive
* data such as bitmaps, the Bundle? provided by onSaveInstanceState() will not suffice*/

class PicViewModel : ViewModel(){
    private var image : Drawable? = null

    fun getImage(): Drawable? = image

    fun setImage(image: Drawable) {
        this.image = image
    }
}
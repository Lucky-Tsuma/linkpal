package com.lucky.fundiapp

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class WorkerSignup2ViewModel : ViewModel() {
    private var image : Drawable? = null
    private var jobField : String? = null
    private var jobField0 : Int? = null
    private var location : String? = null
    private var location0 : Int? = null


    fun getImage(): Drawable? = image

    fun setImage(image: Drawable) {
        this.image = image
    }

    fun getJobField() : String? = jobField

    fun setJobField(jobField: String) {
        this.jobField = jobField
    }

    fun getJobField0() : Int? = jobField0

    fun setJobField0(jobField0: Int) {
        this.jobField0 = jobField0
    }

    fun getLocation() : String? = location

    fun setLocation(location: String) {
        this.location = location
    }

    fun getLocation0() : Int? = location0

    fun setLocation0(location0: Int) {
        this.location0 = location0
    }


}
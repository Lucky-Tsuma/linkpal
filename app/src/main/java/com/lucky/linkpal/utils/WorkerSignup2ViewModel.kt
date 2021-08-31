package com.lucky.linkpal.utils

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class WorkerSignup2ViewModel : ViewModel() {
    private var image: Drawable? = null
    private var jobField: String? = null
    private var jobField0: Int? = null

    fun getImage(): Drawable? = image

    fun setImage(image: Drawable) {
        this.image = image
    }

    fun getJobField(): String? = jobField

    fun setJobField(jobField: String) {
        this.jobField = jobField
    }

    fun getJobField0(): Int? = jobField0

    fun setJobField0(jobField0: Int) {
        this.jobField0 = jobField0
    }
}

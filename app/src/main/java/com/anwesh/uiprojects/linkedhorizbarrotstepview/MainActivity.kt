package com.anwesh.uiprojects.linkedhorizbarrotstepview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.horizbarrotstepview.HorizBarRotStepView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : HorizBarRotStepView = HorizBarRotStepView.create(this)
    }
}

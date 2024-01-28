package com.lottoapp.lottoapp3_0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NumbSelectionActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)

        val numbersText = findViewById<TextView>(R.id.numbersTV)
        val numbersPicker = findViewById<NumberPicker>(R.id.picker1).apply {
            maxValue = 49 // max value of picked number
            minValue = 1  // min value of picked number
        }
        val selectButton = findViewById<Button>(R.id.btnSelect)
        val getRichButton = findViewById<Button>(R.id.btnRich)

        val numbersArray = IntArray(6)
        var i = 0
        var text = ""

        selectButton.setOnClickListener {
            val selectedNumber = numbersPicker.value
            if (selectedNumber in numbersArray) {
                Toast.makeText(this, "Number $selectedNumber has already been chosen. Please select a different number.", Toast.LENGTH_SHORT).show()
            } else {
                numbersArray[i++] = selectedNumber
                text += " $selectedNumber"
                numbersText.text = text
            }
            if (i > numbersArray.size - 1) {
                selectButton.isEnabled = false
                getRichButton.isEnabled = true
            }
        }

        getRichButton.setOnClickListener {
            val intentGoToNumbDrawing = Intent(this, NumbDrawingActivity::class.java)
            intentGoToNumbDrawing.putExtra("SELECTEDNUMBERS", numbersArray)
            if (i < numbersArray.size - 1){
                Toast.makeText(this, "You need to pick six numbers!", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(intentGoToNumbDrawing)
            }
        }
    }
}

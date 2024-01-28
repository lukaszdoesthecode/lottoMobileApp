package com.lottoapp.lottoapp3_0

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class NumbDrawingActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private lateinit var btnGetStats: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_drawing)

        var chosenNumbers = intent.getIntArrayExtra("SELECTEDNUMBERS") ?: IntArray(0)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnGetNumbers = findViewById<Button>(R.id.btnGetNumbers)
        val prize_TV = findViewById<TextView>(R.id.prize_TV)
        btnGetStats = findViewById<Button>(R.id.btnStats)


        progressBar.max = 6
        val delayMillis = 2000L
        val handler = Handler(Looper.getMainLooper())

        val buttons = listOf(
            findViewById<Button>(R.id.button1),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6)
        )

        buttons.forEach { it.visibility = View.INVISIBLE }


        var score = 0
        btnGetNumbers.setOnClickListener {
            val currentInstant = Instant.now()
            btnGetNumbers.isEnabled = false
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedTime = formatter.format(currentInstant.atZone(ZoneId.systemDefault()))


            Thread {
                var progressStatus = 0
                val drawingNumbs = lotto()

                for (button in buttons) {
                    progressStatus += 1
                    handler.post {
                        progressBar.progress = progressStatus
                        val drawnNumber = drawingNumbs[progressStatus - 1]
                        button.text = drawnNumber.toString()
                        if (chosenNumbers != null) {
                            if (drawnNumber in chosenNumbers) {
                                button.setBackgroundColor(Color.GREEN)
                                button.setTextColor(Color.WHITE)
                                score++
                            } else {
                                button.setBackgroundColor(Color.RED)
                                button.setTextColor(Color.WHITE)
                            }
                        }
                        button.visibility = View.VISIBLE
                    }
                    try {
                        Thread.sleep(delayMillis)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {
                    btnGetNumbers.isEnabled = true
                    if(score != 0){
                        val prize = calculateWin(score)
                        prize_TV.text = "You have won $prize$"
                    }
                    updateFirestore(score, drawingNumbs, chosenNumbers)
                }
            }.start()

        }
        btnGetStats.setOnClickListener{
            val intentGoToStats = Intent(this, GamesList::class.java)
            startActivity(intentGoToStats)
        }

    }

    private fun updateFirestore(score: Int, drawingNumbs: IntArray, chosenNumbs: IntArray) {
        val newGameSession = hashMapOf(
            "score" to score,
            "drawnNumbers" to drawingNumbs.toList(),
            "chosenNumbs" to chosenNumbs.toList()
        )

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val gameDocumentRef = db.collection("user").document(userId).collection("games").document("gameData")

        gameDocumentRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, get the current game sessions and append the new one
                    val currentSessions = document.data?.get("sessions") as? MutableList<HashMap<String, Any>> ?: mutableListOf()
                    currentSessions.add(newGameSession)
                    gameDocumentRef.update("sessions", currentSessions)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Game session updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update game session!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Document does not exist, create it with the new game session as the first entry
                    gameDocumentRef.set(mapOf("sessions" to listOf(newGameSession)))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Game session created!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to create game session!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching game data!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun calculateWin(score: Int): Double {
        // Logic to calculate win based on score
        return score * 1000.0
    }

    private fun lotto(n: Int = 6, m: Int = 10): IntArray {
        return if (m < n) {
            IntArray(n)
        } else {
            val numbers = IntArray(n)
            var iterator = 0
            var check: Boolean
            do {
                val number = (1..m).random()
                check = number !in numbers
                if (check) {
                    numbers[iterator++] = number
                }
            } while (iterator < n)
            numbers
        }
    }

}

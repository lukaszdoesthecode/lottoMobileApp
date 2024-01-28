package com.lottoapp.lottoapp3_0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth


class GamesList : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var gamesArrayList: ArrayList<Game>
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games_list)

        userRecyclerView = findViewById(R.id.recyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)

        gamesArrayList = ArrayList()
        adapter = MyAdapter(gamesArrayList)
        userRecyclerView.adapter = adapter

        // Call a function to retrieve games from Firestore
        retrieveGamesFromFirestore()
    }

    private fun retrieveGamesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val gameDocumentRef = db.collection("user").document(userId).collection("games")
                .document("gameData")

            gameDocumentRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val sessions =
                            documentSnapshot.data?.get("sessions") as? List<HashMap<String, Any>>
                                ?: mutableListOf()

                        // Convert data from Firestore to Game objects
                        gamesArrayList.clear()
                        for (session in sessions) {
                            val chosenNumbs = session["chosenNumbs"] as? List<Int>
                            val drawnNumbers = session["drawnNumbers"] as? List<Int>
                            val score = session["score"] as? Long

                            val game = Game(
                                chosenNumbs?.toIntArray(),
                                drawnNumbers?.toIntArray(),
                                score?.toInt()
                            )
                            gamesArrayList.add(game)
                        }

                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

}

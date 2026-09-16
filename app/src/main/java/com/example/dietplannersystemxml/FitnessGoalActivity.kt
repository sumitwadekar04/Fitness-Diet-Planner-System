package com.example.dietplannersystemxml

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FitnessGoalActivity : AppCompatActivity() {

    private lateinit var cardLoseWeight: LinearLayout
    private lateinit var cardMaintainWeight: LinearLayout
    private lateinit var cardGainWeight: LinearLayout
    private lateinit var continueButton: Button

    private var selectedGoal = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fitness_goal)

        // Connect XML views
        cardLoseWeight = findViewById(R.id.cardLoseWeight)
        cardMaintainWeight = findViewById(R.id.cardMaintainWeight)
        cardGainWeight = findViewById(R.id.cardGainWeight)
        continueButton = findViewById(R.id.btnGoalContinue)

        // Get USER_ID from User Details screen
        val userId = intent.getLongExtra("USER_ID", -1)

        // Get user details from User Details screen
        val name = intent.getStringExtra("NAME") ?: ""
        val age = intent.getStringExtra("AGE") ?: ""
        val gender = intent.getStringExtra("GENDER") ?: ""
        val height = intent.getStringExtra("HEIGHT") ?: ""
        val weight = intent.getStringExtra("WEIGHT") ?: ""

        // Lose Weight
        cardLoseWeight.setOnClickListener {

            selectedGoal = "Lose Weight"

            updateSelectedCard()

            Toast.makeText(
                this,
                "Lose Weight selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Maintain Weight
        cardMaintainWeight.setOnClickListener {

            selectedGoal = "Maintain Weight"

            updateSelectedCard()

            Toast.makeText(
                this,
                "Maintain Weight selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Gain Weight
        cardGainWeight.setOnClickListener {

            selectedGoal = "Gain Weight"

            updateSelectedCard()

            Toast.makeText(
                this,
                "Gain Weight selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Continue button
        continueButton.setOnClickListener {

            if (selectedGoal.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please select a fitness goal",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Check USER_ID
                if (userId == -1L) {

                    Toast.makeText(
                        this,
                        "User information not found",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                // Open database
                val databaseHelper = DatabaseHelper(this)
                val database = databaseHelper.writableDatabase

                // Prepare goal data
                val values = ContentValues()
                values.put("goal", selectedGoal)

                // Update the correct user using USER_ID
                database.update(
                    "users",
                    values,
                    "id = ?",
                    arrayOf(userId.toString())
                )

                database.close()

                // Open Fitness Summary
                val intent = Intent(
                    this,
                    FitnessSummaryActivity::class.java
                )

                // Pass USER_ID forward
                intent.putExtra(
                    "USER_ID",
                    userId
                )

                // Pass user details
                intent.putExtra("NAME", name)
                intent.putExtra("AGE", age)
                intent.putExtra("GENDER", gender)
                intent.putExtra("HEIGHT", height)
                intent.putExtra("WEIGHT", weight)

                // Pass selected goal
                intent.putExtra(
                    "GOAL",
                    selectedGoal
                )

                startActivity(intent)
            }
        }
    }

    private fun updateSelectedCard() {

        // Reset all cards
        cardLoseWeight.setBackgroundColor(Color.WHITE)
        cardMaintainWeight.setBackgroundColor(Color.WHITE)
        cardGainWeight.setBackgroundColor(Color.WHITE)

        // Highlight selected card
        when (selectedGoal) {

            "Lose Weight" -> {
                cardLoseWeight.setBackgroundColor(
                    Color.rgb(232, 245, 233)
                )
            }

            "Maintain Weight" -> {
                cardMaintainWeight.setBackgroundColor(
                    Color.rgb(232, 245, 233)
                )
            }

            "Gain Weight" -> {
                cardGainWeight.setBackgroundColor(
                    Color.rgb(232, 245, 233)
                )
            }
        }

        // Show Continue button
        continueButton.visibility = View.VISIBLE
    }
}
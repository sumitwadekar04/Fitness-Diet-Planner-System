package com.example.dietplannersystemxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class WorkoutTypeActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var homeWorkoutCard: LinearLayout
    private lateinit var gymWorkoutCard: LinearLayout

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_workout_type)

        // Connect XML views
        backButton =
            findViewById(R.id.txtWorkoutBack)

        homeWorkoutCard =
            findViewById(R.id.homeWorkoutCard)

        gymWorkoutCard =
            findViewById(R.id.gymWorkoutCard)


        // Get USER_ID from Dashboard

        userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )


        // Back to Dashboard

        backButton.setOnClickListener {

            finish()
        }


        // Home Workout

        homeWorkoutCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    HomeWorkoutActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }


        // Gym Workout

        gymWorkoutCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    GymWorkoutActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }
    }
}
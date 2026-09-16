package com.example.dietplannersystemxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.round

class FitnessSummaryActivity : AppCompatActivity() {

    private lateinit var txtGreeting: TextView
    private lateinit var txtBMI: TextView
    private lateinit var txtBMICategory: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtGender: TextView
    private lateinit var txtHeight: TextView
    private lateinit var txtWeight: TextView
    private lateinit var txtGoal: TextView
    private lateinit var btnSummaryContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fitness_summary)

        // Connect XML views
        txtGreeting =
            findViewById(R.id.txtGreeting)

        txtBMI =
            findViewById(R.id.txtBMI)

        txtBMICategory =
            findViewById(R.id.txtBMICategory)

        txtAge =
            findViewById(R.id.txtAge)

        txtGender =
            findViewById(R.id.txtGender)

        txtHeight =
            findViewById(R.id.txtHeight)

        txtWeight =
            findViewById(R.id.txtWeight)

        txtGoal =
            findViewById(R.id.txtGoal)

        btnSummaryContinue =
            findViewById(R.id.btnSummaryContinue)

        // Get USER_ID from Fitness Goal screen
        val userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )

        // Check USER_ID
        if (userId == -1L) {

            Toast.makeText(
                this,
                "User information not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        // Open database
        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.readableDatabase

        // Get this specific user's information
        val cursor =
            database.query(
                "users",
                arrayOf(
                    "name",
                    "age",
                    "gender",
                    "height",
                    "weight",
                    "goal"
                ),
                "id = ?",
                arrayOf(userId.toString()),
                null,
                null,
                null
            )

        var name = ""
        var age = ""
        var gender = ""
        var height = ""
        var weight = ""
        var goal = ""

        if (cursor.moveToFirst()) {

            name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                )

            age =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow("age")
                ).toString()

            gender =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("gender")
                )

            height =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow("height")
                ).toString()

            weight =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow("weight")
                ).toString()

            goal =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("goal")
                ) ?: ""
        }

        cursor.close()
        database.close()

        // Display user information
        txtGreeting.text =
            "Hello, $name"

        txtAge.text =
            "$age years"

        txtGender.text =
            gender

        txtHeight.text =
            "$height cm"

        txtWeight.text =
            "$weight kg"

        txtGoal.text =
            goal

        // Calculate BMI
        val heightValue =
            height.toFloatOrNull() ?: 0f

        val weightValue =
            weight.toFloatOrNull() ?: 0f

        val bmi =
            if (
                heightValue > 0 &&
                weightValue > 0
            ) {

                weightValue / (
                        (heightValue / 100f) *
                                (heightValue / 100f)
                        )

            } else {
                0f
            }

        // Display BMI
        if (bmi > 0) {

            val roundedBMI =
                round(bmi * 10) / 10

            txtBMI.text =
                roundedBMI.toString()

            val category =
                when {

                    bmi < 18.5 ->
                        "Underweight"

                    bmi < 25 ->
                        "Normal Weight"

                    bmi < 30 ->
                        "Overweight"

                    else ->
                        "Obesity"
                }

            txtBMICategory.text =
                category

        } else {

            txtBMI.text =
                "--"

            txtBMICategory.text =
                "Not available"
        }

        // Continue to Dashboard
        btnSummaryContinue.setOnClickListener {

            /*
             * SAVE LOGIN ONLY NOW
             *
             * The user has completed:
             *
             * User Details
             *      ↓
             * Fitness Goal
             *      ↓
             * Fitness Summary
             *      ↓
             * Dashboard
             *
             * Therefore, this is the correct point
             * to remember the logged-in user.
             */

            val preferences =
                getSharedPreferences(
                    "DietPlannerPrefs",
                    MODE_PRIVATE
                )

            preferences.edit()
                .putLong(
                    "LOGGED_IN_USER_ID",
                    userId
                )
                .apply()

            // Open Dashboard
            val intent =
                Intent(
                    this,
                    DashboardActivity::class.java
                )

            // Send USER_ID to Dashboard
            intent.putExtra(
                "USER_ID",
                userId
            )

            // Send user information
            intent.putExtra(
                "NAME",
                name
            )

            intent.putExtra(
                "GOAL",
                goal
            )

            startActivity(intent)

            finish()
        }
    }
}
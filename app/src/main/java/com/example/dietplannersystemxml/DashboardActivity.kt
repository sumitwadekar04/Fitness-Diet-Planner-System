package com.example.dietplannersystemxml

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private var userId: Long = -1L

    private lateinit var txtDashboardGreeting: TextView
    private lateinit var txtDashboardSubtitle: TextView
    private lateinit var txtDashboardGoal: TextView
    private lateinit var txtWaterProgress: TextView
    private lateinit var txtProgressWeight: TextView

    private lateinit var waterCard: LinearLayout
    private lateinit var progressCard: LinearLayout
    private lateinit var workoutCard: LinearLayout
    private lateinit var dietCard: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        databaseHelper = DatabaseHelper(this)


        // =========================
        // GET USER ID
        // =========================

        userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )


        if (userId == -1L) {

            val preferences =
                getSharedPreferences(
                    "DietPlannerPrefs",
                    MODE_PRIVATE
                )

            userId =
                preferences.getLong(
                    "LOGGED_IN_USER_ID",
                    -1L
                )
        }


        // =========================
        // CHECK LOGIN
        // =========================

        if (userId == -1L) {

            Toast.makeText(
                this,
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()

            return
        }


        // =========================
        // FIND VIEWS
        // =========================

        txtDashboardGreeting =
            findViewById(
                R.id.txtDashboardGreeting
            )

        txtDashboardSubtitle =
            findViewById(
                R.id.txtDashboardSubtitle
            )

        txtDashboardGoal =
            findViewById(
                R.id.txtDashboardGoal
            )

        txtWaterProgress =
            findViewById(
                R.id.txtWaterProgress
            )

        txtProgressWeight =
            findViewById(
                R.id.txtProgressWeight
            )

        waterCard =
            findViewById(
                R.id.waterCard
            )

        progressCard =
            findViewById(
                R.id.progressCard
            )

        workoutCard =
            findViewById(
                R.id.workoutCard
            )

        dietCard =
            findViewById(
                R.id.dietCard
            )


        // =========================
        // LOAD DATA
        // =========================

        loadUserData()

        loadWaterProgress()

        loadProgressWeight()


        // =========================
        // WATER
        // =========================

        waterCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    WaterIntakeActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }


        // =========================
        // PROGRESS
        // =========================

        progressCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ProgressActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }


        // =========================
        // WORKOUT
        // =========================

        workoutCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    WorkoutTypeActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }


        // =========================
        // DIET
        // =========================

        dietCard.setOnClickListener {

            val intent =
                Intent(
                    this,
                    DietActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            startActivity(intent)
        }


        // =========================
        // LOGOUT
        // =========================

        val btnLogout =
            findViewById<TextView>(
                R.id.btnLogout
            )


        btnLogout.setOnClickListener {

            val preferences =
                getSharedPreferences(
                    "DietPlannerPrefs",
                    MODE_PRIVATE
                )


            preferences.edit()
                .remove("LOGGED_IN_USER_ID")
                .apply()


            Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()


            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )


            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK


            startActivity(intent)

            finish()
        }
    }


    // =========================
    // LOAD USER DATA
    // =========================

    private fun loadUserData() {

        val database =
            databaseHelper.readableDatabase


        val cursor =
            database.rawQuery(
                """
                SELECT name, goal
                FROM users
                WHERE id = ?
                """.trimIndent(),
                arrayOf(
                    userId.toString()
                )
            )


        if (cursor.moveToFirst()) {

            val name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "name"
                    )
                )


            val goalIndex =
                cursor.getColumnIndex(
                    "goal"
                )


            val goal =
                if (goalIndex >= 0) {

                    cursor.getString(
                        goalIndex
                    ) ?: "Fitness"

                } else {

                    "Fitness"
                }


            txtDashboardGreeting.text =
                "Good ${getTimeGreeting()} 👋 $name"


            txtDashboardSubtitle.text =
                "Let's work on your goal today."


            txtDashboardGoal.text =
                if (goal.isNotEmpty()) {

                    goal

                } else {

                    "Fitness"
                }
        }


        cursor.close()

        database.close()
    }


    // =========================
    // TIME GREETING
    // =========================

    private fun getTimeGreeting(): String {

        val hour =
            Calendar.getInstance()
                .get(Calendar.HOUR_OF_DAY)


        return when {

            hour < 12 ->
                "Morning"

            hour < 17 ->
                "Afternoon"

            else ->
                "Evening"
        }
    }


    // =========================
    // WATER PROGRESS
    // =========================

    private fun loadWaterProgress() {

        try {

            val database =
                databaseHelper.readableDatabase


            val today =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(
                    Calendar.getInstance().time
                )


            val cursor =
                database.rawQuery(
                    """
                    SELECT amount
                    FROM water
                    WHERE user_id = ? AND date = ?
                    """.trimIndent(),
                    arrayOf(
                        userId.toString(),
                        today
                    )
                )


            var totalWater = 0.0


            while (cursor.moveToNext()) {

                totalWater +=
                    cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                            "amount"
                        )
                    )
            }


            cursor.close()

            database.close()


            txtWaterProgress.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f / 3 L",
                    totalWater
                )

        } catch (e: Exception) {

            txtWaterProgress.text =
                "0 / 3 L"
        }
    }


    // =========================
    // PROGRESS WEIGHT
    // =========================

    private fun loadProgressWeight() {

        try {

            val database =
                databaseHelper.readableDatabase


            val userCursor =
                database.rawQuery(
                    """
                    SELECT weight
                    FROM users
                    WHERE id = ?
                    """.trimIndent(),
                    arrayOf(
                        userId.toString()
                    )
                )


            var weight = 0.0


            if (userCursor.moveToFirst()) {

                weight =
                    userCursor.getDouble(
                        userCursor.getColumnIndexOrThrow(
                            "weight"
                        )
                    )
            }


            userCursor.close()


            val progressCursor =
                database.rawQuery(
                    """
                    SELECT weight
                    FROM progress
                    WHERE user_id = ?
                    ORDER BY id DESC
                    LIMIT 1
                    """.trimIndent(),
                    arrayOf(
                        userId.toString()
                    )
                )


            if (progressCursor.moveToFirst()) {

                weight =
                    progressCursor.getDouble(
                        progressCursor.getColumnIndexOrThrow(
                            "weight"
                        )
                    )
            }


            progressCursor.close()

            database.close()


            txtProgressWeight.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f kg",
                    weight
                )

        } catch (e: Exception) {

            txtProgressWeight.text =
                "-- kg"
        }
    }


    // =========================
    // REFRESH WHEN RETURNING
    // =========================

    override fun onResume() {

        super.onResume()


        if (::txtWaterProgress.isInitialized) {

            loadWaterProgress()
        }


        if (::txtProgressWeight.isInitialized) {

            loadProgressWeight()
        }
    }
}
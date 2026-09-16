package com.example.dietplannersystemxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Always show the original Get Started screen
        setContentView(R.layout.activity_main)

        val getStartedButton =
            findViewById<Button>(R.id.btnGetStarted)

        getStartedButton.setOnClickListener {

            val preferences =
                getSharedPreferences(
                    "DietPlannerPrefs",
                    MODE_PRIVATE
                )

            val loggedInUserId =
                preferences.getLong(
                    "LOGGED_IN_USER_ID",
                    -1L
                )

            if (loggedInUserId != -1L) {

                // Returning user
                val intent =
                    Intent(
                        this,
                        DashboardActivity::class.java
                    )

                intent.putExtra(
                    "USER_ID",
                    loggedInUserId
                )

                startActivity(intent)

            } else {

                // New user / logged out user
                val intent =
                    Intent(
                        this,
                        LoginActivity::class.java
                    )

                startActivity(intent)
            }
        }
    }
}
package com.example.dietplannersystemxml

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaterIntakeActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var waterAmountText: TextView
    private lateinit var addWaterButton: Button
    private lateinit var removeWaterButton: Button

    private var waterIntake = 0

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_water_intake)

        // Connect XML views
        backButton =
            findViewById(R.id.btnWaterBack)

        waterAmountText =
            findViewById(R.id.txtWaterAmount)

        addWaterButton =
            findViewById(R.id.btnAddWater)

        removeWaterButton =
            findViewById(R.id.btnRemoveWater)


        // Get USER_ID from Dashboard
        userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )


        // Load today's water amount
        loadWaterAmount()

        updateWaterAmount()


        // Back button
        backButton.setOnClickListener {
            finish()
        }


        // Add water
        addWaterButton.setOnClickListener {

            waterIntake += 250

            saveWaterAmount()

            updateWaterAmount()
        }


        // Remove water
        removeWaterButton.setOnClickListener {

            if (waterIntake >= 250) {

                waterIntake -= 250

            } else {

                waterIntake = 0
            }

            saveWaterAmount()

            updateWaterAmount()
        }
    }


    // Get today's date
    private fun getTodayDate(): String {

        val dateFormat =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        return dateFormat.format(Date())
    }


    // Load today's water amount for this user
    private fun loadWaterAmount() {

        if (userId == -1L) {
            waterIntake = 0
            return
        }

        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.readableDatabase

        val cursor =
            database.query(
                "water",
                arrayOf("amount"),
                "user_id = ? AND date = ?",
                arrayOf(
                    userId.toString(),
                    getTodayDate()
                ),
                null,
                null,
                null
            )

        if (cursor.moveToFirst()) {

            waterIntake =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "amount"
                    )
                )
        } else {

            waterIntake = 0
        }

        cursor.close()
        database.close()
    }


    // Save today's water amount
    private fun saveWaterAmount() {

        if (userId == -1L) {
            return
        }

        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.writableDatabase

        val today =
            getTodayDate()


        // Check whether today's record already exists
        val cursor =
            database.query(
                "water",
                arrayOf("id"),
                "user_id = ? AND date = ?",
                arrayOf(
                    userId.toString(),
                    today
                ),
                null,
                null,
                null
            )


        val values =
            ContentValues()

        values.put(
            "user_id",
            userId
        )

        values.put(
            "amount",
            waterIntake
        )

        values.put(
            "date",
            today
        )


        if (cursor.moveToFirst()) {

            // Update existing record

            val recordId =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "id"
                    )
                )

            database.update(
                "water",
                values,
                "id = ?",
                arrayOf(
                    recordId.toString()
                )
            )

        } else {

            // Create new record

            database.insert(
                "water",
                null,
                values
            )
        }


        cursor.close()
        database.close()
    }


    // Update screen
    private fun updateWaterAmount() {

        waterAmountText.text =
            "$waterIntake ml"
    }
}
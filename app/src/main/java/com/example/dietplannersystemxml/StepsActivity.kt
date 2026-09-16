package com.example.dietplannersystemxml

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepsActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var stepsAmountText: TextView
    private lateinit var stepsInput: EditText
    private lateinit var saveStepsButton: Button

    private var steps = 0

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_steps)

        // Connect XML views

        backButton =
            findViewById(R.id.btnStepsBack)

        stepsAmountText =
            findViewById(R.id.txtStepsAmount)

        stepsInput =
            findViewById(R.id.edtSteps)

        saveStepsButton =
            findViewById(R.id.btnSaveSteps)


        // Get USER_ID from Dashboard

        userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )


        // Load today's steps

        loadSteps()

        updateSteps()


        // Back button

        backButton.setOnClickListener {

            finish()
        }


        // Save Steps

        saveStepsButton.setOnClickListener {

            val enteredSteps =
                stepsInput.text.toString().trim()

            if (enteredSteps.isNotEmpty()) {

                val enteredValue =
                    enteredSteps.toIntOrNull()

                if (enteredValue != null &&
                    enteredValue >= 0
                ) {

                    steps = enteredValue

                    saveSteps()

                    updateSteps()

                    stepsInput.text.clear()
                }
            }
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


    // Load today's steps for this user

    private fun loadSteps() {

        if (userId == -1L) {

            steps = 0

            return
        }


        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.readableDatabase


        val cursor =
            database.query(
                "steps",
                arrayOf("step_count"),
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

            steps =
                cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        "step_count"
                    )
                )

        } else {

            steps = 0
        }


        cursor.close()
        database.close()
    }


    // Save today's steps

    private fun saveSteps() {

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
                "steps",
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
            "step_count",
            steps
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
                "steps",
                values,
                "id = ?",
                arrayOf(
                    recordId.toString()
                )
            )

        } else {

            // Create new record

            database.insert(
                "steps",
                null,
                values
            )
        }


        cursor.close()
        database.close()
    }


    // Update screen

    private fun updateSteps() {

        stepsAmountText.text =
            steps.toString()
    }
}
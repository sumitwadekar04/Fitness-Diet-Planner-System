package com.example.dietplannersystemxml

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeWorkoutActivity : AppCompatActivity() {

    private lateinit var backButton: Button

    // Day cards
    private lateinit var mondayCard: LinearLayout
    private lateinit var tuesdayCard: LinearLayout
    private lateinit var wednesdayCard: LinearLayout
    private lateinit var thursdayCard: LinearLayout
    private lateinit var fridayCard: LinearLayout
    private lateinit var saturdayCard: LinearLayout
    private lateinit var sundayCard: LinearLayout

    // Completion buttons
    private lateinit var mondayButton: Button
    private lateinit var tuesdayButton: Button
    private lateinit var wednesdayButton: Button
    private lateinit var thursdayButton: Button
    private lateinit var fridayButton: Button
    private lateinit var saturdayButton: Button
    private lateinit var sundayButton: Button

    // Status text
    private lateinit var mondayStatus: TextView
    private lateinit var tuesdayStatus: TextView
    private lateinit var wednesdayStatus: TextView
    private lateinit var thursdayStatus: TextView
    private lateinit var fridayStatus: TextView
    private lateinit var saturdayStatus: TextView
    private lateinit var sundayStatus: TextView

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home_workout)

        // ==============================
        // CONNECT VIEWS
        // ==============================

        backButton = findViewById(R.id.txtHomeWorkoutBack)

        mondayCard = findViewById(R.id.mondayCard)
        tuesdayCard = findViewById(R.id.tuesdayCard)
        wednesdayCard = findViewById(R.id.wednesdayCard)
        thursdayCard = findViewById(R.id.thursdayCard)
        fridayCard = findViewById(R.id.fridayCard)
        saturdayCard = findViewById(R.id.saturdayCard)
        sundayCard = findViewById(R.id.sundayCard)

        mondayButton = findViewById(R.id.btnMondayComplete)
        tuesdayButton = findViewById(R.id.btnTuesdayComplete)
        wednesdayButton = findViewById(R.id.btnWednesdayComplete)
        thursdayButton = findViewById(R.id.btnThursdayComplete)
        fridayButton = findViewById(R.id.btnFridayComplete)
        saturdayButton = findViewById(R.id.btnSaturdayComplete)
        sundayButton = findViewById(R.id.btnSundayComplete)

        mondayStatus = findViewById(R.id.txtMondayStatus)
        tuesdayStatus = findViewById(R.id.txtTuesdayStatus)
        wednesdayStatus = findViewById(R.id.txtWednesdayStatus)
        thursdayStatus = findViewById(R.id.txtThursdayStatus)
        fridayStatus = findViewById(R.id.txtFridayStatus)
        saturdayStatus = findViewById(R.id.txtSaturdayStatus)
        sundayStatus = findViewById(R.id.txtSundayStatus)

        // ==============================
        // GET USER ID
        // ==============================

        userId = intent.getLongExtra(
            "USER_ID",
            -1L
        )

        // ==============================
        // BACK BUTTON
        // ==============================

        backButton.setOnClickListener {
            finish()
        }

        // ==============================
        // LOAD ALL 7 DAYS
        // ==============================

        loadWorkoutStatus(
            "Monday",
            mondayCard,
            mondayButton,
            mondayStatus
        )

        loadWorkoutStatus(
            "Tuesday",
            tuesdayCard,
            tuesdayButton,
            tuesdayStatus
        )

        loadWorkoutStatus(
            "Wednesday",
            wednesdayCard,
            wednesdayButton,
            wednesdayStatus
        )

        loadWorkoutStatus(
            "Thursday",
            thursdayCard,
            thursdayButton,
            thursdayStatus
        )

        loadWorkoutStatus(
            "Friday",
            fridayCard,
            fridayButton,
            fridayStatus
        )

        loadWorkoutStatus(
            "Saturday",
            saturdayCard,
            saturdayButton,
            saturdayStatus
        )

        loadWorkoutStatus(
            "Sunday",
            sundayCard,
            sundayButton,
            sundayStatus
        )

        // ==============================
        // COMPLETION BUTTONS
        // ==============================

        mondayButton.setOnClickListener {
            markWorkoutCompleted(
                "Monday",
                mondayCard,
                mondayButton,
                mondayStatus
            )
        }

        tuesdayButton.setOnClickListener {
            markWorkoutCompleted(
                "Tuesday",
                tuesdayCard,
                tuesdayButton,
                tuesdayStatus
            )
        }

        wednesdayButton.setOnClickListener {
            markWorkoutCompleted(
                "Wednesday",
                wednesdayCard,
                wednesdayButton,
                wednesdayStatus
            )
        }

        thursdayButton.setOnClickListener {
            markWorkoutCompleted(
                "Thursday",
                thursdayCard,
                thursdayButton,
                thursdayStatus
            )
        }

        fridayButton.setOnClickListener {
            markWorkoutCompleted(
                "Friday",
                fridayCard,
                fridayButton,
                fridayStatus
            )
        }

        saturdayButton.setOnClickListener {
            markWorkoutCompleted(
                "Saturday",
                saturdayCard,
                saturdayButton,
                saturdayStatus
            )
        }

        sundayButton.setOnClickListener {
            markWorkoutCompleted(
                "Sunday",
                sundayCard,
                sundayButton,
                sundayStatus
            )
        }
    }

    // ==============================
    // TODAY'S DATE
    // ==============================

    private fun getTodayDate(): String {

        val dateFormat =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        return dateFormat.format(Date())
    }

    // ==============================
    // DISPLAY DATE
    // ==============================

    private fun getDisplayDate(
        databaseDate: String
    ): String {

        return try {

            val inputFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val outputFormat =
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                )

            val date =
                inputFormat.parse(databaseDate)

            if (date != null) {
                outputFormat.format(date)
            } else {
                databaseDate
            }

        } catch (e: Exception) {

            databaseDate
        }
    }

    // ==============================
    // LOAD WORKOUT STATUS
    // ==============================

    private fun loadWorkoutStatus(
        workoutDay: String,
        card: LinearLayout,
        button: Button,
        statusText: TextView
    ) {

        if (userId == -1L) {
            return
        }

        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.readableDatabase

        val cursor =
            database.query(
                "workout",
                arrayOf(
                    "completion_date",
                    "status"
                ),
                "user_id = ? AND workout_type = ? AND workout_day = ? AND status = ?",
                arrayOf(
                    userId.toString(),
                    "Home",
                    workoutDay,
                    "Completed"
                ),
                null,
                null,
                "id DESC",
                "1"
            )

        if (cursor.moveToFirst()) {

            val completionDate =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "completion_date"
                    )
                )

            showCompletedState(
                card,
                button,
                statusText,
                completionDate
            )

        } else {

            showNotCompletedState(
                card,
                button,
                statusText
            )
        }

        cursor.close()
        database.close()
    }

    // ==============================
    // MARK WORKOUT COMPLETED
    // ==============================

    private fun markWorkoutCompleted(
        workoutDay: String,
        card: LinearLayout,
        button: Button,
        statusText: TextView
    ) {

        if (userId == -1L) {
            return
        }

        val databaseHelper =
            DatabaseHelper(this)

        val database =
            databaseHelper.writableDatabase

        val today =
            getTodayDate()

        // Find existing completed workout
        val cursor =
            database.query(
                "workout",
                arrayOf("id"),
                "user_id = ? AND workout_type = ? AND workout_day = ? AND status = ?",
                arrayOf(
                    userId.toString(),
                    "Home",
                    workoutDay,
                    "Completed"
                ),
                null,
                null,
                "id DESC",
                "1"
            )

        if (cursor.moveToFirst()) {

            // Existing workout found.
            // Update its completion date.

            val workoutId =
                cursor.getLong(
                    cursor.getColumnIndexOrThrow(
                        "id"
                    )
                )

            val values =
                ContentValues()

            values.put(
                "completion_date",
                today
            )

            database.update(
                "workout",
                values,
                "id = ?",
                arrayOf(
                    workoutId.toString()
                )
            )

        } else {

            // No existing workout.
            // Create a new record.

            val values =
                ContentValues()

            values.put(
                "user_id",
                userId
            )

            values.put(
                "workout_type",
                "Home"
            )

            values.put(
                "workout_day",
                workoutDay
            )

            values.put(
                "status",
                "Completed"
            )

            values.put(
                "completion_date",
                today
            )

            database.insert(
                "workout",
                null,
                values
            )
        }

        cursor.close()
        database.close()

        // Immediately show today's date
        showCompletedState(
            card,
            button,
            statusText,
            today
        )
    }

    // ==============================
    // COMPLETED STATE
    // ==============================

    private fun showCompletedState(
        card: LinearLayout,
        button: Button,
        statusText: TextView,
        completionDate: String
    ) {

        // Light green card
        card.setBackgroundColor(
            Color.rgb(
                232,
                245,
                233
            )
        )

        // Dark green button
        button.setBackgroundColor(
            Color.rgb(
                46,
                125,
                50
            )
        )

        button.setTextColor(
            Color.WHITE
        )

        button.text =
            "✓  Workout Completed"

        // Show completion date
        statusText.visibility =
            TextView.VISIBLE

        statusText.text =
            "Completed on ${getDisplayDate(completionDate)}"

        statusText.setTextColor(
            Color.rgb(
                46,
                125,
                50
            )
        )
    }

    // ==============================
    // NOT COMPLETED STATE
    // ==============================

    private fun showNotCompletedState(
        card: LinearLayout,
        button: Button,
        statusText: TextView
    ) {

        // White card
        card.setBackgroundColor(
            Color.WHITE
        )

        // Green button
        button.setBackgroundColor(
            Color.rgb(
                56,
                142,
                60
            )
        )

        button.setTextColor(
            Color.WHITE
        )

        button.text =
            "Mark Workout Completed"

        // Hide completion date
        statusText.visibility =
            TextView.GONE
    }
}
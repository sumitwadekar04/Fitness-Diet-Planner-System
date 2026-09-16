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

class GymWorkoutActivity : AppCompatActivity() {

    private var userId: Long = -1L

    // Monday
    private lateinit var mondayCard: LinearLayout
    private lateinit var mondayStatus: TextView
    private lateinit var mondayButton: Button

    // Tuesday
    private lateinit var tuesdayCard: LinearLayout
    private lateinit var tuesdayStatus: TextView
    private lateinit var tuesdayButton: Button

    // Wednesday
    private lateinit var wednesdayCard: LinearLayout
    private lateinit var wednesdayStatus: TextView
    private lateinit var wednesdayButton: Button

    // Thursday
    private lateinit var thursdayCard: LinearLayout
    private lateinit var thursdayStatus: TextView
    private lateinit var thursdayButton: Button

    // Friday
    private lateinit var fridayCard: LinearLayout
    private lateinit var fridayStatus: TextView
    private lateinit var fridayButton: Button

    // Saturday
    private lateinit var saturdayCard: LinearLayout
    private lateinit var saturdayStatus: TextView
    private lateinit var saturdayButton: Button

    // Sunday
    private lateinit var sundayCard: LinearLayout
    private lateinit var sundayStatus: TextView
    private lateinit var sundayButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_gym_workout
        )

        // ==============================
        // GET USER ID
        // ==============================

        userId =
            intent.getLongExtra(
                "USER_ID",
                -1L
            )

        // ==============================
        // BACK BUTTON
        // ==============================

        val backButton: TextView =
            findViewById(
                R.id.txtGymWorkoutBack
            )

        backButton.setOnClickListener {
            finish()
        }

        // ==============================
        // MONDAY
        // ==============================

        mondayCard =
            findViewById(
                R.id.mondayCard
            )

        mondayStatus =
            findViewById(
                R.id.txtMondayStatus
            )

        mondayButton =
            findViewById(
                R.id.btnMondayComplete
            )

        // ==============================
        // TUESDAY
        // ==============================

        tuesdayCard =
            findViewById(
                R.id.tuesdayCard
            )

        tuesdayStatus =
            findViewById(
                R.id.txtTuesdayStatus
            )

        tuesdayButton =
            findViewById(
                R.id.btnTuesdayComplete
            )

        // ==============================
        // WEDNESDAY
        // ==============================

        wednesdayCard =
            findViewById(
                R.id.wednesdayCard
            )

        wednesdayStatus =
            findViewById(
                R.id.txtWednesdayStatus
            )

        wednesdayButton =
            findViewById(
                R.id.btnWednesdayComplete
            )

        // ==============================
        // THURSDAY
        // ==============================

        thursdayCard =
            findViewById(
                R.id.thursdayCard
            )

        thursdayStatus =
            findViewById(
                R.id.txtThursdayStatus
            )

        thursdayButton =
            findViewById(
                R.id.btnThursdayComplete
            )

        // ==============================
        // FRIDAY
        // ==============================

        fridayCard =
            findViewById(
                R.id.fridayCard
            )

        fridayStatus =
            findViewById(
                R.id.txtFridayStatus
            )

        fridayButton =
            findViewById(
                R.id.btnFridayComplete
            )

        // ==============================
        // SATURDAY
        // ==============================

        saturdayCard =
            findViewById(
                R.id.saturdayCard
            )

        saturdayStatus =
            findViewById(
                R.id.txtSaturdayStatus
            )

        saturdayButton =
            findViewById(
                R.id.btnSaturdayComplete
            )

        // ==============================
        // SUNDAY
        // ==============================

        sundayCard =
            findViewById(
                R.id.sundayCard
            )

        sundayStatus =
            findViewById(
                R.id.txtSundayStatus
            )

        sundayButton =
            findViewById(
                R.id.btnSundayComplete
            )

        // ==============================
        // COMPLETION BUTTONS
        // ==============================

        mondayButton.setOnClickListener {

            saveWorkout("Monday")

            loadWorkoutStatus(
                "Monday",
                mondayCard,
                mondayStatus,
                mondayButton
            )
        }

        tuesdayButton.setOnClickListener {

            saveWorkout("Tuesday")

            loadWorkoutStatus(
                "Tuesday",
                tuesdayCard,
                tuesdayStatus,
                tuesdayButton
            )
        }

        wednesdayButton.setOnClickListener {

            saveWorkout("Wednesday")

            loadWorkoutStatus(
                "Wednesday",
                wednesdayCard,
                wednesdayStatus,
                wednesdayButton
            )
        }

        thursdayButton.setOnClickListener {

            saveWorkout("Thursday")

            loadWorkoutStatus(
                "Thursday",
                thursdayCard,
                thursdayStatus,
                thursdayButton
            )
        }

        fridayButton.setOnClickListener {

            saveWorkout("Friday")

            loadWorkoutStatus(
                "Friday",
                fridayCard,
                fridayStatus,
                fridayButton
            )
        }

        saturdayButton.setOnClickListener {

            saveWorkout("Saturday")

            loadWorkoutStatus(
                "Saturday",
                saturdayCard,
                saturdayStatus,
                saturdayButton
            )
        }

        sundayButton.setOnClickListener {

            saveWorkout("Sunday")

            loadWorkoutStatus(
                "Sunday",
                sundayCard,
                sundayStatus,
                sundayButton
            )
        }

        // ==============================
        // LOAD SAVED STATUS
        // ==============================

        loadAllWorkoutStatus()
    }

    override fun onResume() {

        super.onResume()

        if (userId != -1L) {
            loadAllWorkoutStatus()
        }
    }

    // ==============================
    // TODAY'S DATE
    // ==============================

    private fun getTodayDate(): String {

        val format =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        return format.format(Date())
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
    // SAVE COMPLETED WORKOUT
    // ==============================

    private fun saveWorkout(
        workoutDay: String
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
                    "GYM",
                    workoutDay,
                    "completed"
                ),
                null,
                null,
                "id DESC",
                "1"
            )

        if (cursor.moveToFirst()) {

            // Existing workout found.
            // Update its date.

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
                "GYM"
            )

            values.put(
                "workout_day",
                workoutDay
            )

            values.put(
                "status",
                "completed"
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
    }

    // ==============================
    // LOAD ONE WORKOUT DAY
    // ==============================

    private fun loadWorkoutStatus(
        workoutDay: String,
        card: LinearLayout,
        statusText: TextView,
        completeButton: Button
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
                    "completion_date"
                ),
                "user_id = ? AND workout_type = ? AND workout_day = ? AND status = ?",
                arrayOf(
                    userId.toString(),
                    "GYM",
                    workoutDay,
                    "completed"
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

            // Green background
            card.setBackgroundColor(
                Color.parseColor(
                    "#E8F5E9"
                )
            )

            statusText.text =
                "Completed on ${getDisplayDate(completionDate)}"

            statusText.visibility =
                TextView.VISIBLE

            completeButton.text =
                "✓ Workout Completed"

        } else {

            // White background
            card.setBackgroundColor(
                Color.WHITE
            )

            statusText.text =
                ""

            statusText.visibility =
                TextView.GONE

            completeButton.text =
                "Mark Workout Completed"
        }

        cursor.close()
        database.close()
    }

    // ==============================
    // LOAD ALL 7 DAYS
    // ==============================

    private fun loadAllWorkoutStatus() {

        loadWorkoutStatus(
            "Monday",
            mondayCard,
            mondayStatus,
            mondayButton
        )

        loadWorkoutStatus(
            "Tuesday",
            tuesdayCard,
            tuesdayStatus,
            tuesdayButton
        )

        loadWorkoutStatus(
            "Wednesday",
            wednesdayCard,
            wednesdayStatus,
            wednesdayButton
        )

        loadWorkoutStatus(
            "Thursday",
            thursdayCard,
            thursdayStatus,
            thursdayButton
        )

        loadWorkoutStatus(
            "Friday",
            fridayCard,
            fridayStatus,
            fridayButton
        )

        loadWorkoutStatus(
            "Saturday",
            saturdayCard,
            saturdayStatus,
            saturdayButton
        )

        loadWorkoutStatus(
            "Sunday",
            sundayCard,
            sundayStatus,
            sundayButton
        )
    }
}
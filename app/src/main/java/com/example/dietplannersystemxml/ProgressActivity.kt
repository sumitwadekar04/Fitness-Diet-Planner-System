package com.example.dietplannersystemxml

import android.content.ContentValues
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgressActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private var userId: Long = -1L

    private lateinit var txtStartingWeight: TextView
    private lateinit var txtCurrentWeight: TextView
    private lateinit var txtWeightChange: TextView
    private lateinit var txtBMI: TextView
    private lateinit var txtGoal: TextView

    private lateinit var etWeight: EditText
    private lateinit var btnSaveWeight: Button
    private lateinit var btnBack: Button

    private lateinit var progressHistoryContainer: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_progress)

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

            finish()

            return
        }


        // =========================
        // FIND VIEWS
        // =========================

        btnBack =
            findViewById(R.id.btnBack)

        txtStartingWeight =
            findViewById(R.id.txtStartingWeight)

        txtCurrentWeight =
            findViewById(R.id.txtCurrentWeight)

        txtWeightChange =
            findViewById(R.id.txtWeightChange)

        txtBMI =
            findViewById(R.id.txtCurrentBMI)

        txtGoal =
            findViewById(R.id.txtFitnessGoal)

        etWeight =
            findViewById(R.id.edtCurrentWeight)

        btnSaveWeight =
            findViewById(R.id.btnSaveProgress)

        progressHistoryContainer =
            findViewById(R.id.historyContainer)


        // =========================
        // STYLE BACK BUTTON
        // =========================

        val backBackground =
            GradientDrawable()

        backBackground.shape =
            GradientDrawable.RECTANGLE

        backBackground.cornerRadius =
            14f

        backBackground.setColor(
            Color.rgb(
                232,
                245,
                233
            )
        )

        btnBack.background =
            backBackground

        btnBack.setTextColor(
            Color.rgb(
                20,
                90,
                36
            )
        )


        // =========================
        // BACK BUTTON
        // =========================

        btnBack.setOnClickListener {

            finish()
        }


        // =========================
        // LOAD DATA
        // =========================

        loadProgress()


        // =========================
        // SAVE WEIGHT
        // =========================

        btnSaveWeight.setOnClickListener {

            saveWeight()
        }
    }


    // ==========================================
    // LOAD PROGRESS
    // ==========================================

    private fun loadProgress() {

        val database =
            databaseHelper.readableDatabase


        // =========================
        // GET USER INFORMATION
        // =========================

        val userCursor =
            database.rawQuery(
                """
                SELECT weight, height, goal
                FROM users
                WHERE id = ?
                """.trimIndent(),
                arrayOf(
                    userId.toString()
                )
            )


        var startingWeight = 0.0
        var height = 0.0
        var goal = "Fitness"


        if (userCursor.moveToFirst()) {

            startingWeight =
                userCursor.getDouble(
                    userCursor.getColumnIndexOrThrow(
                        "weight"
                    )
                )

            height =
                userCursor.getDouble(
                    userCursor.getColumnIndexOrThrow(
                        "height"
                    )
                )

            val goalIndex =
                userCursor.getColumnIndex(
                    "goal"
                )

            if (goalIndex >= 0) {

                goal =
                    userCursor.getString(
                        goalIndex
                    ) ?: "Fitness"
            }
        }


        userCursor.close()


        // =========================
        // GET LATEST WEIGHT
        // =========================

        val progressCursor =
            database.rawQuery(
                """
                SELECT weight, bmi
                FROM progress
                WHERE user_id = ?
                ORDER BY id DESC
                LIMIT 1
                """.trimIndent(),
                arrayOf(
                    userId.toString()
                )
            )


        var currentWeight =
            startingWeight

        var currentBMI =
            calculateBMI(
                startingWeight,
                height
            )


        if (progressCursor.moveToFirst()) {

            currentWeight =
                progressCursor.getDouble(
                    progressCursor.getColumnIndexOrThrow(
                        "weight"
                    )
                )

            currentBMI =
                progressCursor.getDouble(
                    progressCursor.getColumnIndexOrThrow(
                        "bmi"
                    )
                )
        }


        progressCursor.close()


        // =========================
        // DISPLAY SUMMARY
        // =========================

        txtStartingWeight.text =
            String.format(
                Locale.getDefault(),
                "%.1f kg",
                startingWeight
            )


        txtCurrentWeight.text =
            String.format(
                Locale.getDefault(),
                "%.1f kg",
                currentWeight
            )


        val change =
            currentWeight - startingWeight


        txtWeightChange.text =
            when {

                change > 0 ->

                    String.format(
                        Locale.getDefault(),
                        "+%.1f kg",
                        change
                    )

                change < 0 ->

                    String.format(
                        Locale.getDefault(),
                        "%.1f kg",
                        change
                    )

                else ->
                    "No change"
            }


        txtBMI.text =
            String.format(
                Locale.getDefault(),
                "%.1f",
                currentBMI
            )


        txtGoal.text =
            goal


        database.close()


        // =========================
        // LOAD HISTORY
        // =========================

        loadHistory()
    }


    // ==========================================
    // SAVE NEW WEIGHT
    // ==========================================

    private fun saveWeight() {

        val weightText =
            etWeight.text.toString().trim()


        if (weightText.isEmpty()) {

            etWeight.error =
                "Enter your weight"

            return
        }


        val weight =
            weightText.toDoubleOrNull()


        if (weight == null || weight <= 0) {

            etWeight.error =
                "Enter a valid weight"

            return
        }


        val database =
            databaseHelper.writableDatabase


        // =========================
        // GET HEIGHT
        // =========================

        val cursor =
            database.rawQuery(
                """
                SELECT height
                FROM users
                WHERE id = ?
                """.trimIndent(),
                arrayOf(
                    userId.toString()
                )
            )


        var height = 0.0


        if (cursor.moveToFirst()) {

            height =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "height"
                    )
                )
        }


        cursor.close()


        if (height <= 0) {

            database.close()

            Toast.makeText(
                this,
                "Height information not found",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // =========================
        // CALCULATE BMI
        // =========================

        val bmi =
            calculateBMI(
                weight,
                height
            )


        // =========================
        // TODAY'S DATE
        // =========================

        val today =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(
                Calendar.getInstance().time
            )


        // =========================
        // SAVE PROGRESS
        // =========================

        val values =
            ContentValues()


        values.put(
            "user_id",
            userId
        )


        values.put(
            "weight",
            weight
        )


        values.put(
            "bmi",
            bmi
        )


        values.put(
            "date",
            today
        )


        val result =
            database.insert(
                "progress",
                null,
                values
            )


        database.close()


        if (result != -1L) {

            Toast.makeText(
                this,
                "Progress updated successfully",
                Toast.LENGTH_SHORT
            ).show()


            etWeight.text.clear()


            loadProgress()

        } else {

            Toast.makeText(
                this,
                "Unable to save progress",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // ==========================================
    // BMI CALCULATION
    // ==========================================

    private fun calculateBMI(
        weight: Double,
        heightCm: Double
    ): Double {

        val heightMeter =
            heightCm / 100.0


        if (heightMeter <= 0) {

            return 0.0
        }


        return weight /
                (heightMeter * heightMeter)
    }


    // ==========================================
    // LOAD WEIGHT HISTORY
    // ==========================================

    private fun loadHistory() {

        progressHistoryContainer.removeAllViews()


        val database =
            databaseHelper.readableDatabase


        val cursor =
            database.rawQuery(
                """
                SELECT weight, bmi, date
                FROM progress
                WHERE user_id = ?
                ORDER BY id DESC
                """.trimIndent(),
                arrayOf(
                    userId.toString()
                )
            )


        // =========================
        // NO HISTORY
        // =========================

        if (!cursor.moveToFirst()) {

            val emptyText =
                TextView(this)


            emptyText.text =
                "No progress updates yet.\nUpdate your weight to start tracking."


            emptyText.textSize =
                14f


            emptyText.setTextColor(
                Color.DKGRAY
            )


            emptyText.setPadding(
                0,
                15,
                0,
                15
            )


            progressHistoryContainer.addView(
                emptyText
            )


            cursor.close()

            database.close()

            return
        }


        // =========================
        // DISPLAY HISTORY
        // =========================

        do {

            val weight =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "weight"
                    )
                )


            val bmi =
                cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                        "bmi"
                    )
                )


            val date =
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        "date"
                    )
                )


            // =========================
            // HISTORY ROW
            // =========================

            val historyCard =
                LinearLayout(this)


            historyCard.orientation =
                LinearLayout.HORIZONTAL


            historyCard.setBackgroundColor(
                Color.WHITE
            )


            historyCard.setPadding(
                15,
                15,
                15,
                15
            )


            // =========================
            // DATE
            // =========================

            val dateText =
                TextView(this)


            dateText.text =
                date


            dateText.textSize =
                14f


            dateText.setTextColor(
                Color.DKGRAY
            )


            val dateParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )


            historyCard.addView(
                dateText,
                dateParams
            )


            // =========================
            // WEIGHT
            // =========================

            val weightTextView =
                TextView(this)


            weightTextView.text =
                String.format(
                    Locale.getDefault(),
                    "%.1f kg",
                    weight
                )


            weightTextView.textSize =
                15f


            weightTextView.setTextColor(
                Color.rgb(
                    20,
                    90,
                    36
                )
            )


            weightTextView.setTypeface(
                null,
                android.graphics.Typeface.BOLD
            )


            historyCard.addView(
                weightTextView
            )


            // =========================
            // BMI
            // =========================

            val bmiTextView =
                TextView(this)


            bmiTextView.text =
                String.format(
                    Locale.getDefault(),
                    "   BMI %.1f",
                    bmi
                )


            bmiTextView.textSize =
                14f


            bmiTextView.setTextColor(
                Color.DKGRAY
            )


            historyCard.addView(
                bmiTextView
            )


            // =========================
            // HISTORY ROW MARGIN
            // =========================

            val params =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )


            params.setMargins(
                0,
                0,
                0,
                10
            )


            progressHistoryContainer.addView(
                historyCard,
                params
            )


        } while (cursor.moveToNext())


        cursor.close()

        database.close()
    }
}
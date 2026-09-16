package com.example.dietplannersystemxml

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var etLoginId: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etGender: AutoCompleteTextView
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var btnContinue: Button
    private lateinit var tvError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_user_details
        )

        etLoginId =
            findViewById(R.id.etLoginId)

        etPassword =
            findViewById(R.id.etPassword)

        etName =
            findViewById(R.id.etName)

        etAge =
            findViewById(R.id.etAge)

        etGender =
            findViewById(R.id.etGender)

        etHeight =
            findViewById(R.id.etHeight)

        etWeight =
            findViewById(R.id.etWeight)

        btnContinue =
            findViewById(R.id.btnContinue)

        tvError =
            findViewById(R.id.tvError)

        // Gender dropdown options
        val genderOptions =
            arrayOf(
                "Male",
                "Female",
                "Other"
            )

        val genderAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genderOptions
            )

        etGender.setAdapter(
            genderAdapter
        )

        etGender.setTextColor(
            Color.rgb(
                34,
                34,
                34
            )
        )

        etGender.setHintTextColor(
            Color.rgb(
                136,
                136,
                136
            )
        )

        etGender.setOnClickListener {

            etGender.showDropDown()
        }

        etGender.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                etGender.showDropDown()
            }
        }

        btnContinue.setOnClickListener {

            val loginId =
                etLoginId.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()
                    .trim()

            val name =
                etName.text
                    .toString()
                    .trim()

            val age =
                etAge.text
                    .toString()
                    .trim()

            val gender =
                etGender.text
                    .toString()
                    .trim()

            val height =
                etHeight.text
                    .toString()
                    .trim()

            val weight =
                etWeight.text
                    .toString()
                    .trim()

            // Check empty fields
            if (
                loginId.isEmpty() ||
                password.isEmpty() ||
                name.isEmpty() ||
                age.isEmpty() ||
                gender.isEmpty() ||
                height.isEmpty() ||
                weight.isEmpty()
            ) {

                tvError.text =
                    "Please fill in all details"

                tvError.visibility =
                    View.VISIBLE

                return@setOnClickListener
            }

            // Check valid gender
            if (
                gender != "Male" &&
                gender != "Female" &&
                gender != "Other"
            ) {

                tvError.text =
                    "Please select a valid gender"

                tvError.visibility =
                    View.VISIBLE

                return@setOnClickListener
            }

            val ageValue =
                age.toIntOrNull()

            val heightValue =
                height.toFloatOrNull()

            val weightValue =
                weight.toFloatOrNull()

            if (
                ageValue == null ||
                heightValue == null ||
                weightValue == null
            ) {

                tvError.text =
                    "Please enter valid age, height and weight"

                tvError.visibility =
                    View.VISIBLE

                return@setOnClickListener
            }

            val databaseHelper =
                DatabaseHelper(this)

            val database =
                databaseHelper.writableDatabase

            // Check Login ID
            val checkCursor =
                database.query(
                    "users",
                    arrayOf("id"),
                    "login_id = ?",
                    arrayOf(loginId),
                    null,
                    null,
                    null
                )

            if (checkCursor.moveToFirst()) {

                checkCursor.close()
                database.close()

                tvError.text =
                    "This Login ID is already taken"

                tvError.visibility =
                    View.VISIBLE

                return@setOnClickListener
            }

            checkCursor.close()

            // Insert user
            val values =
                ContentValues()

            values.put(
                "login_id",
                loginId
            )

            values.put(
                "password",
                password
            )

            values.put(
                "name",
                name
            )

            values.put(
                "age",
                ageValue
            )

            values.put(
                "gender",
                gender
            )

            values.put(
                "height",
                heightValue
            )

            values.put(
                "weight",
                weightValue
            )

            val userId =
                database.insert(
                    "users",
                    null,
                    values
                )

            database.close()

            if (userId == -1L) {

                tvError.text =
                    "Unable to create account"

                tvError.visibility =
                    View.VISIBLE

                return@setOnClickListener
            }

            /*
             * IMPORTANT:
             *
             * Do NOT save LOGGED_IN_USER_ID here.
             *
             * The user still has to complete:
             *
             * User Details
             *      ↓
             * Fitness Goal
             *      ↓
             * Fitness Summary
             *      ↓
             * Dashboard
             */

            // Open Fitness Goal
            val intent =
                Intent(
                    this,
                    FitnessGoalActivity::class.java
                )

            intent.putExtra(
                "USER_ID",
                userId
            )

            intent.putExtra(
                "NAME",
                name
            )

            intent.putExtra(
                "AGE",
                age
            )

            intent.putExtra(
                "GENDER",
                gender
            )

            intent.putExtra(
                "HEIGHT",
                height
            )

            intent.putExtra(
                "WEIGHT",
                weight
            )

            startActivity(intent)

            finish()
        }
    }
}
package com.example.dietplannersystemxml

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var etLoginId: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnCreateAccount: Button
    private lateinit var txtError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)

        etLoginId =
            findViewById(R.id.etLoginId)

        etPassword =
            findViewById(R.id.etPassword)

        btnLogin =
            findViewById(R.id.btnLogin)

        btnCreateAccount =
            findViewById(R.id.btnCreateAccount)

        txtError =
            findViewById(R.id.txtError)

        // =========================
        // LOGIN
        // =========================

        btnLogin.setOnClickListener {

            val loginId =
                etLoginId.text
                    .toString()
                    .trim()

            val password =
                etPassword.text
                    .toString()
                    .trim()

            txtError.text = ""

            if (loginId.isEmpty()) {

                txtError.text =
                    "Please enter Login ID"

                return@setOnClickListener
            }

            if (password.isEmpty()) {

                txtError.text =
                    "Please enter password"

                return@setOnClickListener
            }

            val database =
                databaseHelper.readableDatabase

            val cursor =
                database.rawQuery(
                    """
                    SELECT id, name, goal
                    FROM users
                    WHERE login_id = ? AND password = ?
                    """.trimIndent(),
                    arrayOf(
                        loginId,
                        password
                    )
                )

            if (cursor.moveToFirst()) {

                val userId =
                    cursor.getLong(
                        cursor.getColumnIndexOrThrow("id")
                    )

                val name =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                    )

                val goalIndex =
                    cursor.getColumnIndex("goal")

                val goal =
                    if (goalIndex >= 0) {
                        cursor.getString(goalIndex)
                            ?: ""
                    } else {
                        ""
                    }

                cursor.close()

                // =========================
                // SAVE LOGIN
                // =========================

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

                // =========================
                // OPEN DASHBOARD
                // =========================

                val intent =
                    Intent(
                        this,
                        DashboardActivity::class.java
                    )

                intent.putExtra(
                    "USER_ID",
                    userId
                )

                intent.putExtra(
                    "USER_NAME",
                    name
                )

                intent.putExtra(
                    "USER_GOAL",
                    goal
                )

                startActivity(intent)

                finish()

            } else {

                cursor.close()

                txtError.text =
                    "Invalid Login ID or Password"
            }
        }

        // =========================
        // CREATE NEW ACCOUNT
        // =========================

        btnCreateAccount.setOnClickListener {

            val intent =
                Intent(
                    this,
                    UserDetailsActivity::class.java
                )

            startActivity(intent)
        }
    }
}
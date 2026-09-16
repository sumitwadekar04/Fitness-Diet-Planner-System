package com.example.dietplannersystemxml

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(
        context,
        "DietPlanner.db",
        null,
        10
    ) {

    override fun onCreate(db: SQLiteDatabase) {

        // =========================
        // USERS TABLE
        // =========================

        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                login_id TEXT UNIQUE,
                password TEXT,
                name TEXT,
                age INTEGER,
                gender TEXT,
                height REAL,
                weight REAL,
                goal TEXT
            )
            """.trimIndent()
        )


        // =========================
        // WATER TABLE
        // =========================

        db.execSQL(
            """
            CREATE TABLE water (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                amount INTEGER,
                date TEXT
            )
            """.trimIndent()
        )


        // =========================
        // STEPS TABLE
        // =========================
        // Kept for database compatibility.
        // The Steps section is no longer used
        // in the application.

        db.execSQL(
            """
            CREATE TABLE steps (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                step_count INTEGER,
                date TEXT
            )
            """.trimIndent()
        )


        // =========================
        // WORKOUT TABLE
        // =========================

        db.execSQL(
            """
            CREATE TABLE workout (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                workout_type TEXT,
                workout_day TEXT,
                status TEXT,
                completion_date TEXT
            )
            """.trimIndent()
        )


        // =========================
        // PROGRESS TABLE
        // =========================

        db.execSQL(
            """
            CREATE TABLE progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                weight REAL,
                bmi REAL,
                date TEXT
            )
            """.trimIndent()
        )
    }


    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        // ==========================================
        // ADD LOGIN ID IF MISSING
        // ==========================================

        if (!columnExists(db, "users", "login_id")) {
            db.execSQL(
                "ALTER TABLE users ADD COLUMN login_id TEXT"
            )
        }


        // ==========================================
        // ADD PASSWORD IF MISSING
        // ==========================================

        if (!columnExists(db, "users", "password")) {
            db.execSQL(
                "ALTER TABLE users ADD COLUMN password TEXT"
            )
        }


        // ==========================================
        // ADD WORKOUT COLUMNS IF MISSING
        // ==========================================

        if (!columnExists(db, "workout", "workout_day")) {
            db.execSQL(
                "ALTER TABLE workout ADD COLUMN workout_day TEXT"
            )
        }


        if (!columnExists(db, "workout", "completion_date")) {
            db.execSQL(
                "ALTER TABLE workout ADD COLUMN completion_date TEXT"
            )
        }


        // ==========================================
        // CREATE LOGIN ID INDEX
        // ==========================================

        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS
            idx_users_login_id
            ON users(login_id)
            WHERE login_id IS NOT NULL
            """.trimIndent()
        )


        // ==========================================
        // CREATE PROGRESS TABLE
        // ==========================================

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS progress (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                weight REAL,
                bmi REAL,
                date TEXT
            )
            """.trimIndent()
        )
    }


    // ==========================================
    // CHECK IF COLUMN EXISTS
    // ==========================================

    private fun columnExists(
        db: SQLiteDatabase,
        tableName: String,
        columnName: String
    ): Boolean {

        val cursor =
            db.rawQuery(
                "PRAGMA table_info($tableName)",
                null
            )

        var exists = false

        while (cursor.moveToNext()) {

            val name =
                cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
                )

            if (name.equals(columnName, ignoreCase = true)) {
                exists = true
                break
            }
        }

        cursor.close()

        return exists
    }
}
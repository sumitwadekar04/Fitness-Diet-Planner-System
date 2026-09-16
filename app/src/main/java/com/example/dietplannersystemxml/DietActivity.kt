
package com.example.dietplannersystemxml

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DietActivity : AppCompatActivity() {

    private lateinit var backButton: Button

    private lateinit var dietTitle: TextView
    private lateinit var breakfastText: TextView
    private lateinit var lunchText: TextView
    private lateinit var snackText: TextView
    private lateinit var dinnerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_diet)

        // Back button
        backButton = findViewById(R.id.txtDietBack)

        backButton.setOnClickListener {
            finish()
        }

        // Connect views
        dietTitle = findViewById(R.id.txtDietTitle)
        breakfastText = findViewById(R.id.txtBreakfast)
        lunchText = findViewById(R.id.txtLunch)
        snackText = findViewById(R.id.txtSnack)
        dinnerText = findViewById(R.id.txtDinner)

        // Get user's goal
        val goal = intent.getStringExtra("GOAL") ?: ""

        // Update diet according to goal
        when (goal) {

            "Lose Weight" -> {

                dietTitle.text = "Weight Loss Diet"

                breakfastText.text =
                    "• Oats with milk or yogurt\n" +
                            "• One serving of fruit\n" +
                            "• Eggs or another protein source\n" +
                            "• Water"

                lunchText.text =
                    "• 1–2 chapatis or a moderate rice portion\n" +
                            "• Dal or another protein source\n" +
                            "• Plenty of vegetables\n" +
                            "• Salad or curd"

                snackText.text =
                    "• One fruit\n" +
                            "• Yogurt or a small handful of nuts\n" +
                            "• Water"

                dinnerText.text =
                    "• Chapati or a moderate rice portion\n" +
                            "• Dal, paneer, eggs, or another protein source\n" +
                            "• Plenty of vegetables\n" +
                            "• Salad"

            }

            "Maintain Weight" -> {

                dietTitle.text = "Weight Maintenance Diet"

                breakfastText.text =
                    "• Eggs or paneer\n" +
                            "• Whole-grain bread or oats\n" +
                            "• One serving of fruit\n" +
                            "• Water or milk"

                lunchText.text =
                    "• Rice or chapati\n" +
                            "• Dal or another protein source\n" +
                            "• Vegetables\n" +
                            "• Salad or curd"

                snackText.text =
                    "• Fruit\n" +
                            "• Nuts or seeds\n" +
                            "• Yogurt or milk\n" +
                            "• Plenty of water"

                dinnerText.text =
                    "• Chapati or rice\n" +
                            "• Dal, paneer, eggs, or another protein source\n" +
                            "• Cooked vegetables\n" +
                            "• Salad"

            }

            "Gain Weight" -> {

                dietTitle.text = "Weight Gain Diet"

                breakfastText.text =
                    "• Oats with milk\n" +
                            "• Eggs or paneer\n" +
                            "• Banana or other fruit\n" +
                            "• Nuts or nut butter"

                lunchText.text =
                    "• Rice or chapati\n" +
                            "• Dal, paneer, eggs, or another protein source\n" +
                            "• Vegetables\n" +
                            "• Curd or yogurt"

                snackText.text =
                    "• Banana with milk or yogurt\n" +
                            "• Nuts or seeds\n" +
                            "• Peanut butter sandwich"

                dinnerText.text =
                    "• Rice or chapati\n" +
                            "• Dal, paneer, eggs, or another protein source\n" +
                            "• Cooked vegetables\n" +
                            "• Curd or yogurt"

            }

            else -> {

                dietTitle.text = "Your Diet Plan"

            }
        }
    }
}


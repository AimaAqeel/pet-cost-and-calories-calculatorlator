package com.example.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import android.view.LayoutInflater
import android.graphics.Color
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var petTypeSpinner: Spinner
    private lateinit var breedEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var ageUnitSpinner: Spinner
    private lateinit var growthStageSpinner: Spinner
    private lateinit var weightEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var activityLevelSpinner: Spinner

    private lateinit var foodNameEditText: EditText
    private lateinit var caloriesEditText: EditText
    private lateinit var costEditText: EditText

    private lateinit var calculateButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        petTypeSpinner = findViewById(R.id.petTypeSpinner)
        breedEditText = findViewById(R.id.breedEditText)
        ageEditText = findViewById(R.id.ageEditText)
        ageUnitSpinner = findViewById(R.id.ageUnitSpinner)
        growthStageSpinner = findViewById(R.id.growthStageSpinner)
        weightEditText = findViewById(R.id.weightEditText)
        heightEditText = findViewById(R.id.heightEditText)
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner)

        foodNameEditText = findViewById(R.id.foodNameEditText)
        caloriesEditText = findViewById(R.id.caloriesEditText)
        costEditText = findViewById(R.id.costEditText)

        calculateButton = findViewById(R.id.calculateButton)

        // Set up spinners
        setupSpinners()

        calculateButton.setOnClickListener {
            calculateNutrition()
        }

        // Show welcome dialog when app starts
        showWelcomeDialog()
    }

    private fun setupSpinners() {
        // Pet Types
        val petTypes = listOf(
            "Dog", "Cat", "Rabbit", "Bird", "Hamster",
            "Guinea Pig", "Turtle", "Fish", "Ferret", "Lizard"
        )
        val petAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, petTypes)
        petAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        petTypeSpinner.adapter = petAdapter

        // Growth Stages
        val growthStages = listOf("Baby", "Puppy/Kitten", "Adult", "Senior")
        val growthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, growthStages)
        growthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        growthStageSpinner.adapter = growthAdapter

        // Activity Levels
        val activityLevels = listOf("Low", "Medium", "High")
        val activityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, activityLevels)
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activityLevelSpinner.adapter = activityAdapter
    }

    private fun calculateNutrition() {
        try {
            val weight = weightEditText.text.toString().toDoubleOrNull() ?: 0.0
            val caloriesPer100g = caloriesEditText.text.toString().toDoubleOrNull() ?: 0.0
            val costPerKg = costEditText.text.toString().toDoubleOrNull() ?: 0.0
            val activityLevel = activityLevelSpinner.selectedItem.toString()
            val foodName = foodNameEditText.text.toString().takeIf { it.isNotEmpty() } ?: "food"

            if (weight == 0.0 || caloriesPer100g == 0.0 || costPerKg == 0.0) {
                Toast.makeText(this, "Please enter valid weight, calories, and cost.", Toast.LENGTH_SHORT).show()
                return
            }

            val activityMultiplier = when (activityLevel.lowercase()) {
                "low" -> 70
                "medium" -> 90
                "high" -> 110
                else -> 85
            }

            val dailyCalories = weight * activityMultiplier
            val gramsPerDay = (dailyCalories / caloriesPer100g) * 100
            val kgPerMonth = (gramsPerDay / 1000.0) * 30
            val monthlyCost = kgPerMonth * costPerKg

            showResultsDialog(
                dailyCalories.toInt(),
                gramsPerDay.toInt(),
                kgPerMonth,
                monthlyCost,
                foodName
            )

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showResultsDialog(
        dailyCalories: Int,
        dailyGrams: Int,
        monthlyKg: Double,
        monthlyCost: Double,
        foodName: String
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_results, null)

        // Set emojis and text values for each row
        val petTypeEmoji = when (petTypeSpinner.selectedItem.toString().lowercase()) {
            "dog" -> "🐕"
            "cat" -> "🐱"
            "rabbit" -> "🐰"
            "bird" -> "🐦"
            "hamster" -> "🐹"
            "guinea pig" -> "🐹"
            "turtle" -> "🐢"
            "fish" -> "🐠"
            "ferret" -> "🦦"
            "lizard" -> "🦎"
            else -> "🐾"
        }

        dialogView.findViewById<TextView>(R.id.tvPetEmoji).text = petTypeEmoji
        dialogView.findViewById<TextView>(R.id.tvCalorieEmoji).text = "ℹ️"
        dialogView.findViewById<TextView>(R.id.tvDailyFoodEmoji).text = "🧮"
        dialogView.findViewById<TextView>(R.id.tvMonthlyFoodEmoji).text = "📦"
        dialogView.findViewById<TextView>(R.id.tvMonthlyCostEmoji).text = "💲"

        // Set the text values
        dialogView.findViewById<TextView>(R.id.tvDailyCalories).text = "$dailyCalories kcal"
        dialogView.findViewById<TextView>(R.id.tvDailyFood).text = "$dailyGrams grams of $foodName"
        dialogView.findViewById<TextView>(R.id.tvMonthlyFood).text = "${"%.2f".format(monthlyKg)} kg"
        dialogView.findViewById<TextView>(R.id.tvMonthlyCost).text = "$${String.format("%.2f", monthlyCost)}"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Set up close button
        dialogView.findViewById<Button>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showWelcomeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_welcome, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set up get started button
        dialogView.findViewById<Button>(R.id.btnGetStarted).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
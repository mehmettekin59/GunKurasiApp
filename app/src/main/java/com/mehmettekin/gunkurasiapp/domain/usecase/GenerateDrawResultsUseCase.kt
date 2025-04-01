package com.mehmettekin.gunkurasiapp.domain.usecase


import android.os.Build
import androidx.annotation.RequiresApi
import com.mehmettekin.gunkurasiapp.domain.model.DrawResult
import com.mehmettekin.gunkurasiapp.domain.model.DrawSettings
import com.mehmettekin.gunkurasiapp.domain.model.ItemType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

class GenerateDrawResultsUseCase @Inject constructor() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    operator fun invoke(settings: DrawSettings): List<DrawResult> {
        val participants = settings.participants.toMutableList()
        val results = mutableListOf<DrawResult>()

        // Calculate people per month
        val peoplePerMonth = if (settings.durationMonths < settings.participantCount) {
            // Multiple people per month
            settings.participantCount / settings.durationMonths
        } else {
            // One person per month (or fewer months than participants)
            1
        }

        // Calculate months needed
        val monthsNeeded = if (peoplePerMonth == 1) {
            settings.participantCount
        } else {
            settings.durationMonths
        }

        // Calculate amount per person
        val amountPerPerson = if (peoplePerMonth == 1) {
            // Each person gets the full monthly amount
            settings.monthlyAmount * settings.participantCount
        } else {
            // Each person gets a share of the monthly amount
            settings.monthlyAmount * settings.durationMonths / settings.participantCount
        }

        // Format for currency and gold
        val currencyFormat = when (settings.itemType) {
            ItemType.TL -> "%.2f ₺"
            ItemType.CURRENCY -> "%.2f %s"
            ItemType.GOLD -> "%.4f %s"
        }

        // Get specific item code if needed
        val specificItem = when (settings.itemType) {
            ItemType.TL -> ""
            else -> settings.specificItem
        }

        // Format amount based on type
        val formattedAmount = when (settings.itemType) {
            ItemType.TL ->
                String.format(Locale.getDefault(), currencyFormat, amountPerPerson)
            else ->
                String.format(Locale.getDefault(), currencyFormat, amountPerPerson, specificItem)
        }

        // Starting month and year
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, settings.startYear)
        calendar.set(Calendar.MONTH, settings.startMonth - 1) // 0-based month

        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        // Shuffle participants for random draw
        participants.shuffle(Random(System.currentTimeMillis()))

        // Last person is special case - they get the last month automatically
        val lastPerson = participants.removeLast()

        // Generate results for each month
        for (monthIndex in 0 until monthsNeeded - 1) { // -1 for last person
            val monthName = dateFormat.format(calendar.time)

            // For each person this month
            for (personIndex in 0 until peoplePerMonth) {
                if (participants.isEmpty()) break

                val participant = participants.removeAt(0)
                results.add(
                    DrawResult(
                        participantId = participant.id,
                        participantName = participant.name,
                        month = monthName,
                        amount = formattedAmount
                    )
                )
            }

            // Advance to next month
            calendar.add(Calendar.MONTH, 1)
        }

        // Last month is for the last person
        val lastMonthName = dateFormat.format(calendar.time)
        results.add(
            DrawResult(
                participantId = lastPerson.id,
                participantName = lastPerson.name,
                month = lastMonthName,
                amount = formattedAmount
            )
        )

        return results
    }
}
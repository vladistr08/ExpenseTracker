package com.example.expensetracker.Enums

enum class TimeFrame(val value: String) {
    DAY("Day"), MONTH("Month"), YEAR("Year");

    companion object {
        val values: Array<String?>
            // Method to get an array of time frame values for the spinner
            get() {
                val timeFrames = values()
                val values = arrayOfNulls<String>(timeFrames.size)
                for (i in timeFrames.indices) {
                    values[i] = timeFrames[i].value
                }
                return values
            }
    }
}


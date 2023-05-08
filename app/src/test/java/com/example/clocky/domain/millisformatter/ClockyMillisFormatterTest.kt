package com.example.clocky.domain.millisformatter

import org.junit.Test

class ClockyMillisFormatterTest{

    private val millisFormatter = MillisInFullTimeStringFormatter()

    @Test
    fun `Millis formatting test`(){
        // given a loop that contains the millis values for seconds in the range (10s..19s)
        for(millis in 10_000L..19_000L step 1000L){
            // when formatting the millis
            val formattedMinutes = millisFormatter.formatMillis(millis)
                .substring(6..7)
            val expectedSecondsString = "${millis/1000}"
            // the time must be correctly formatted
            assert(formattedMinutes == expectedSecondsString)
        }
    }

}
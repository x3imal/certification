package com.gridnine.testing;

import com.gridnine.testing.flight.Flight;
import com.gridnine.testing.flightbuilder.FlightBuilder;
import com.gridnine.testing.segment.Segment;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class FilterLongGroundTimeTest {
    @Test
    public void testFilterDepartureBeforeNow() {
        List<Flight> testFlights = FlightBuilder.createFlights();
        List<Flight> filteredFlights = Main.filterDepartureBeforeNow(testFlights);

        for (Flight flight : filteredFlights) {
            assertTrue(flight.getSegments().stream()
                    .allMatch(segment -> segment.getDepartureDate().isAfter(LocalDateTime.now())));
        }
    }

    @Test
    public void testFilterArrivalBeforeDeparture() {
        List<Flight> testFlights = FlightBuilder.createFlights();
        List<Flight> filteredFlights = Main.filterArrivalBeforeDeparture(testFlights);

        for (Flight flight : filteredFlights) {
            assertTrue(flight.getSegments().stream()
                    .allMatch(segment -> segment.getArrivalDate().isAfter(segment.getDepartureDate())));
        }
    }

    @Test
    public void testFilterLongGroundTime() {
        List<Flight> testFlights = FlightBuilder.createFlights();
        List<Flight> filteredFlights = Main.filterLongGroundTime(testFlights);

        for (Flight flight : filteredFlights) {
            List<Segment> segments = flight.getSegments();
            long totalGroundTime = IntStream.range(1, segments.size())
                    .mapToLong(i -> Duration.between(segments.get(i - 1)
                            .getArrivalDate(), segments.get(i)
                            .getDepartureDate()).toHours())
                    .sum();
            if (totalGroundTime > 2) {
                System.out.println("Failed Flight: " + flight);
            }
            assertTrue(totalGroundTime <= 2);
        }
    }
}

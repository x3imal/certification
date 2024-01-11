package com.gridnine.testing;

import com.gridnine.testing.flight.Flight;
import com.gridnine.testing.flightbuilder.FlightBuilder;
import com.gridnine.testing.segment.Segment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static final String MSG_FLIGHTS_DEPARTURE_BEFORE_NOW = "Flights with departure before now:";
    private static final String MSG_FLIGHTS_ARRIVAL_BEFORE_DEPARTURE = "Flights with arrival before departure:";
    private static final String MSG_FLIGHTS_LONG_GROUND_TIME = "Flights with more than two hours ground time:";

    public static void main(String[] args) {

        List<Flight> flights = FlightBuilder.createFlights();

        printFilteredFlights(flights, MSG_FLIGHTS_DEPARTURE_BEFORE_NOW, Main::filterDepartureBeforeNow);
        printFilteredFlights(flights, MSG_FLIGHTS_ARRIVAL_BEFORE_DEPARTURE, Main::filterArrivalBeforeDeparture);
        printFilteredFlights(flights, MSG_FLIGHTS_LONG_GROUND_TIME, Main::filterLongGroundTime);
    }

    // Вылет до текущего момента времени
    public static List<Flight> filterDepartureBeforeNow(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .noneMatch(segment ->
                                segment.getDepartureDate().isBefore(LocalDateTime.now())))
                .collect(Collectors.toList());
    }

    // Сегменты с датой прилета раньше даты вылета
    public static List<Flight> filterArrivalBeforeDeparture(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> flight.getSegments().stream()
                        .noneMatch(segment ->
                                segment.getArrivalDate().isBefore(segment.getDepartureDate())))
                .collect(Collectors.toList());
    }

    // Общее время на земле превышает два часа
    public static List<Flight> filterLongGroundTime(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> {
                    List<Segment> segments = flight.getSegments();
                    long totalGroundTime = IntStream.range(1, segments.size())
                            .mapToLong(i -> Duration.between(segments.get(i - 1)
                                    .getArrivalDate(), segments.get(i)
                                    .getDepartureDate()).toMinutes())
                            .sum();
                    return totalGroundTime <= 120;
                })
                .collect(Collectors.toList());
    }

    //вывод
    private static void printFilteredFlights(List<Flight> flights, String message,
                                             java.util.function.Function<List<Flight>,
                                                     List<Flight>> filterFunction) {
        System.out.println(message);
        filterFunction.apply(flights).forEach(System.out::println);
    }
}

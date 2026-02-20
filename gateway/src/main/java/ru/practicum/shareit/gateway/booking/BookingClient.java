package ru.practicum.shareit.gateway.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.gateway.booking.dto.NewBookingDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;
import ru.practicum.shareit.gateway.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createBooking(long userId, NewBookingDto newBookingDto) {
        String path = "";
        return post(path, userId, newBookingDto);
    }

    public ResponseEntity<Object> approveOrRejectBooking(long userId, long bookingId, Boolean approved) {
        String path = String.format("/%d?approved=%s", bookingId, approved);
        return patch(path, userId);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        String path = String.format("/%d", bookingId);
        return get(path, userId);
    }

    public ResponseEntity<Object> getBookingsForBooker(long userId, BookingState state, Integer page, Integer size) {
        String path = "?state={state}&page={page}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "page", page,
                "size", size
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(long userId, BookingState state, Integer page, Integer size) {
        String path = String.format("/owner?state={state}&page={page}&size={size}");
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "page", page,
                "size", size
        );
        return get(path, userId, parameters);
    }

}

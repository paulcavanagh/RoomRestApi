package com.galvanize.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galvanize.Application;
import com.galvanize.models.Room;
import com.galvanize.repositories.RoomsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import sun.jvm.hotspot.utilities.Assert;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.EMPTY_MAP;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest
public class RoomsApiIntegrationTest {

    RestTemplate restTemplate = new TestRestTemplate();

    final String BASE_URL = "http://localhost:8080/rooms/";

    @Autowired
    private RoomsRepository roomsRepository;

    @Before
    public void initialize() {
        roomsRepository.deleteAll();
    }


    @Test
    public void postWithSuccessStatusCode() {

        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));
    }

    @Test
    public void postRespondsWithFieldsForTheInstance() {
        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

        Room newRoom = response.getBody();
        assertThat(newRoom.getId(), notNullValue());
        assertThat(newRoom.getName(), equalTo(name));
        assertThat(newRoom.getCapacity(), equalTo(capacity));
        assertThat(newRoom.isHasVc(), equalTo(hasVc));
        assertThat(roomsRepository.count(), equalTo(1L));

        room.setName("Python");

        response = restTemplate.postForEntity(BASE_URL, room, Room.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CREATED));

        newRoom = response.getBody();
        assertThat(newRoom.getName(), equalTo("Python"));
        assertThat(roomsRepository.count(), equalTo(2L));
    }

    @Test
    public void postRespondsWithUnprocessibleEntityForMalformedRequest() {
        Room room = new Room();
        room.setName("");
        room.setCapacity(12);
        room.setHasVc(false);
        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);

        assertThat(response.getStatusCode(), equalTo(UNPROCESSABLE_ENTITY));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void postRespondsWithDetailsOfValidationError() throws Exception {
        Room room = new Room();
        room.setName("");
        room.setCapacity(12);
        room.setHasVc(true);

        ResponseEntity<String> response = restTemplate.postForEntity(
                BASE_URL, room, String.class, EMPTY_MAP);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> error = objectMapper.readValue(
                response.getBody(), new TypeReference<Map<String, Object>>() {
                });

        assertThat((String) error.get("reason"), equalTo("Unprocessable Entity"));
        List<Map<String, String>> errors = (List<Map<String, String>>) error.get("errors");
    }

    @Test
    public void GetRespondswithRoomObjectSuccessfulStatusCode() throws Exception {
        Room room = new Room();
        room.setName("Ruby");
        room.setCapacity(10);
        room.setHasVc(true);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);

        ResponseEntity<String> roomsResponse = restTemplate.getForEntity(BASE_URL, String.class);
        String roomsJson = roomsResponse.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Room> roomList = objectMapper.readValue(roomsJson, new TypeReference<List<Room>>() {
        });

        assertThat(roomList.get(0).getName(), equalTo(room.getName()));
        assertThat(roomList.get(0).getCapacity(), equalTo(room.getCapacity()));
        assertThat(roomList.get(0).isHasVc(), equalTo(room.isHasVc()));

        room = new Room();
        room.setName("Python");
        room.setCapacity(12);
        room.setHasVc(false);

        response = restTemplate.postForEntity(BASE_URL, room, Room.class);

        roomsResponse = restTemplate.getForEntity(BASE_URL, String.class);
        roomsJson = roomsResponse.getBody();

        roomList = objectMapper.readValue(roomsJson, new TypeReference<List<Room>>() {
        });

        assertThat(roomList.size(), equalTo(2));
        assertThat(roomList.get(1).getName(), equalTo(room.getName()));
        assertThat(roomList.get(1).getCapacity(), equalTo(room.getCapacity()));
        assertThat(roomList.get(1).isHasVc(), equalTo(room.isHasVc()));
    }

    @Test
    public void GetRespondswithNoRoomObjectSuccessfulStatusCode() throws Exception {

        ResponseEntity<String> roomsResponse = restTemplate.getForEntity(BASE_URL, String.class);
        String roomsJson = roomsResponse.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Room> roomList = objectMapper.readValue(roomsJson, new TypeReference<List<Room>>() {
        });

        assertThat(roomList.size(), equalTo(0));
    }

    @Test
    public void GetByIdWithSuccessStatusCode() {

        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        Room newRoom = response.getBody();

        response = restTemplate.getForEntity(BASE_URL + "/" + newRoom.getId(), Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void GetByIdReturnRoomObject() {

        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        Room newRoom = response.getBody();

        response = restTemplate.getForEntity(BASE_URL + "/" + newRoom.getId(), Room.class);

        Room searchRoom = response.getBody();
        assertThat(searchRoom.getId(), equalTo(newRoom.getId()));
        assertThat(searchRoom.getName(), equalTo(room.getName()));
        assertThat(searchRoom.getCapacity(), equalTo(room.getCapacity()));
        assertThat(searchRoom.isHasVc(), equalTo(room.isHasVc()));

        room = new Room();
        room.setName("Python");
        room.setCapacity(12);
        room.setHasVc(false);

        response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        newRoom = response.getBody();

        response = restTemplate.getForEntity(BASE_URL + "/" + newRoom.getId(), Room.class);

        searchRoom = response.getBody();
        assertThat(searchRoom.getId(), equalTo(newRoom.getId()));
        assertThat(searchRoom.getName(), equalTo(room.getName()));
        assertThat(searchRoom.getCapacity(), equalTo(room.getCapacity()));
        assertThat(searchRoom.isHasVc(), equalTo(room.isHasVc()));

    }

    @Test
    public void GetByIdReturnNotFoundStatusCode() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();

        ResponseEntity<Room> response = restTemplate.getForEntity(BASE_URL + "/" + randomUUIDString, Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    public void PutByIdReturnNoContentHttpStatusCode() {

        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        HttpEntity<Room> entity = new HttpEntity<>(room);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        Room newRoom = response.getBody();

        response = restTemplate.exchange(BASE_URL + "/" + newRoom.getId(), HttpMethod.PUT, entity, Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void PutByIdReturnUpdatedObject() {
        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        room = response.getBody();

        room.setName("Python");
        room.setCapacity(10);
        room.setHasVc(true);
        restTemplate.put(BASE_URL + "/" + room.getId(), room);

        response = restTemplate.getForEntity(BASE_URL + "/" + room.getId(), Room.class);

        Room updatedRoom = response.getBody();

        assertThat(updatedRoom.getId(), equalTo(room.getId()));
        assertThat(updatedRoom.getName(), equalTo(room.getName()));
        assertThat(updatedRoom.getCapacity(), equalTo(room.getCapacity()));
        assertThat(updatedRoom.isHasVc(), equalTo(room.isHasVc()));
    }

    @Test
    public void PutByIdUnprocessableEntity() {
        Room room = new Room();
        room.setName("Oh Happy days!!!");
        room.setCapacity(8);
        room.setHasVc(false);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        Room newRoom = response.getBody();
        newRoom.setName("");

        HttpEntity<Room> entity = new HttpEntity<>(newRoom);
        response = restTemplate.exchange(BASE_URL + "/" + newRoom.getId(), HttpMethod.PUT, entity, Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Test
    public void DeleteByIdReturnNoContentHttpStatusCode() {
        final String name = "Ruby";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        String roomId = response.getBody().getId();

        HttpEntity<Room> entity = new HttpEntity<>(room);
        response = restTemplate.exchange(BASE_URL + "/" + roomId, HttpMethod.DELETE, entity, Room.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }

    @Test
    public void DeleteByIdReturnNoRoomObjects(){
        final String name = "Sunshine on a raining day....";
        final int capacity = 12;
        final boolean hasVc = false;

        Room room = new Room();
        room.setName(name);
        room.setCapacity(capacity);
        room.setHasVc(hasVc);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room, Room.class);
        room = response.getBody();

        restTemplate.delete(BASE_URL + "/" + room.getId(), room);
        response = restTemplate.getForEntity(BASE_URL + "/" + room.getId(), Room.class);

        assertThat(response.getBody().getName(), equalTo(null));
    }

    @Test
    public void DeleteByIdReturnRemainingRoom(){
        Room room1 = new Room();
        room1.setName("red");
        room1.setCapacity(12);
        room1.setHasVc(true);

        Room room2 = new Room();
        room2.setName("blue");
        room2.setCapacity(8);
        room2.setHasVc(false);

        ResponseEntity<Room> response = restTemplate.postForEntity(BASE_URL, room1, Room.class);
        room1 = response.getBody();

        response = restTemplate.postForEntity(BASE_URL, room2, Room.class);
        room2 = response.getBody();

        restTemplate.delete(BASE_URL + "/" + room1.getId(), room1);
        response = restTemplate.getForEntity(BASE_URL + "/" + room2.getId(), Room.class);

        assertThat(room2.getName(), equalTo("blue"));
        assertThat(roomsRepository.count(), equalTo(1L));
    }

}

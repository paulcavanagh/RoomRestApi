package com.galvanize.controllers;

import com.galvanize.models.Room;
import com.galvanize.repositories.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by localadmin on 6/7/16.
 */
@RestController
public class RoomsController {

    @Autowired
    private RoomsRepository roomsRepository;

    @RequestMapping(value = "/rooms", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Room createRoom(@Valid @RequestBody Room room) {
        return roomsRepository.save(room);
    }

    @RequestMapping(value = "/rooms", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Room> getRoom()
    {
        List<Room> roomList = roomsRepository.findAll();
        return roomList;
    }

    @RequestMapping(value="/rooms/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Room getRoomById(@PathVariable("id") String id) throws NotFoundException
    {
        Room searchRoom = roomsRepository.findOne(id);

        if (searchRoom == null) {
            throw new NotFoundException();
        }

        return searchRoom;
    }

    @RequestMapping(value="/rooms/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void UpdateRoomById(@PathVariable String id, @Valid @RequestBody Room room) {
        room.setId(id);
        roomsRepository.save(room);
    }

    @RequestMapping(value="/rooms/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void DeleteRoomById(@PathVariable("id") String id)
    {
        roomsRepository.delete(id);
    }


    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    public Map<String, Object> handleException(MethodArgumentNotValidException exception) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("reason", HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());

        return errorBody;
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Map<String, Object> handleException(NotFoundException exception) {
        Map<String, Object> errorBody = new HashMap<>();

        return errorBody;
    }

    static class NotFoundException extends Exception {
        NotFoundException(){
            super();
        }
    }
}

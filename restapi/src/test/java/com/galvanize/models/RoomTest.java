package com.galvanize.models;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import static org.junit.Assert.assertEquals;

public class RoomTest {

    private Validator validator;

    @Before
    public void init() {
        ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
        this.validator = vf.getValidator();
    }

    @Test
    public void ensuresNameIsNotNull() {
        Room room = new Room();
        room.setName(null);
        Set<ConstraintViolation<Room>> violations = validator.validate(room);
        assertEquals(1, violations.size());
    }

    @Test
     public void ensuresNameIsNotEmpty() {
        Room room = new Room();
        room.setName("");
        Set<ConstraintViolation<Room>> violations = validator.validate(room);
        assertEquals(1, violations.size());
    }
}

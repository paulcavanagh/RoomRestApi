package com.galvanize.models;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

/**
 * Created by localadmin on 6/7/16.
 */
public class Room {

    @NotNull
    @Length(min = 1, message = "Room name must be more than 1 character")
    private String name;
    private int capacity;
    private boolean hasVc;

    public void setId(String id) {
        this.id = id;
    }

    @Id
    private String id;

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setHasVc(boolean hasVc) {
        this.hasVc = hasVc;
    }

    public String getName() {
        return name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public boolean isHasVc() {
        return hasVc;
    }

    public String getId() {
        return id;
    }
}

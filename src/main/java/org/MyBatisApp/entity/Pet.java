package org.MyBatisApp.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class Pet extends NamedEntity {

    private long owner_id;

    public long getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(long owner_id) {
        this.owner_id = owner_id;
    }

    public Pet(String name) {
        this.name = name;
    }

    public Pet(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Pet(long id, String name, long owner_id) {
        this.id = id;
        this.name = name;
        this.owner_id = owner_id;
    }

    public Pet() {

    }

    @Override
    public String toString() {
        return "Pet{" +
                "owner_id=" + owner_id +
                ", id=" + id +
                ", firstName='" + name + '\'' +
                '}';
    }

}
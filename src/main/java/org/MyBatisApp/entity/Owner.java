package org.MyBatisApp.entity;

import lombok.Data;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Owner{

    protected long id;
    protected String firstName;
    private List<Pet> pets;


    public void setPets(List<Pet> pets) {
        for(Pet pet:pets) pet.setOwner_id(id);
        this.pets = pets;
    }

    public Owner addPet(Pet pet){
        if (pets==null) pets = new ArrayList<>();
        pets.add(pet);
        return this;
    }

    public Owner(String firstName) {
        this.firstName = firstName;
    }

    public Owner(long id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    public Owner() {

    }

    @Override
    public String toString() {
        return "Owner{" +
                "pets=" + pets +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return id == owner.id &&
                Objects.equals(firstName, owner.firstName); /*&&
                Objects.equals(pets, owner.pets);*/
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, firstName, pets);
    }
}

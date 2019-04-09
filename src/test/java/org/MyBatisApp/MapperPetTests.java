package org.MyBatisApp;

import org.MyBatisApp.entity.Pet;
import org.MyBatisApp.mapper.PetMapper;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyBatisAppApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MapperPetTests {

    @Autowired
    PetMapper petMapper;

    @Test
    public void test01_fillTableAndFindAll() throws InterruptedException {
        List<Pet> pets = Arrays.asList(new Pet("Вольт"), new Pet("Блэк"), new Pet("Стэла"), new Pet("Мурзик"));
        for (Pet pet:pets) {
            petMapper.create(pet);
        }
        System.out.println("pets = "+pets);
        Assert.assertEquals(4, petMapper.findAll().size());
    }

    @Test
    public void test02_findByIdAndName(){
        String name = "Мурзик";
        Pet pet = petMapper.findOneByName(name);
        Pet petById = petMapper.findOneById(pet.getId());
        Assert.assertEquals(petById, pet);
    }

    @Test
    public void test03_findAndUpdate(){
        String name = "Мурзик";
        Pet pet = petMapper.findOneByName(name);
        Assert.assertEquals(name, pet.getName());

        String newName = "Мурзач";
        pet.setName(newName);
        petMapper.save(pet);
        Pet updatedPet = petMapper.findOneByName(newName);
        Assert.assertEquals(newName, updatedPet.getName());
    }

    @Test
    public void test04_removeAndCount(){
        String name = "Мурзач";
        Pet pet = petMapper.findOneByName(name);
        Assert.assertEquals(name, pet.getName());
        petMapper.delete(pet);
        Assert.assertEquals(3, petMapper.count().intValue());
    }

    @Test
    public void test05_removeListOfRecords(){
        List<Pet> pets = petMapper.findByNames("Вольт", "Стэла");
        Assert.assertEquals(2, pets.size());
        Assert.assertTrue(pets.stream().anyMatch(pet -> pet.getName().equals("Вольт")));
        Assert.assertTrue(pets.stream().anyMatch(pet -> pet.getName().equals("Стэла")));

        petMapper.deleteAllWhereIdIn(pets.stream().map(Pet::getId).collect(Collectors.toList()));
        Assert.assertEquals(1, petMapper.count().intValue());
    }

}
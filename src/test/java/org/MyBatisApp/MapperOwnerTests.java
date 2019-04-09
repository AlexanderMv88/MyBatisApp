package org.MyBatisApp;

import org.MyBatisApp.entity.Owner;
import org.MyBatisApp.entity.Pet;
import org.MyBatisApp.mapper.OwnerMapper;
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
public class MapperOwnerTests {

    @Autowired
    PetMapper petMapper;

    @Autowired
    OwnerMapper ownerMapper;

    @Test
    public void test03_fillTableAndFindAll() throws InterruptedException {
        List<Owner> owners = Arrays.asList(new Owner("Вася"), new Owner("Коля"), new Owner("Петя"), new Owner("Марина"));
        for (Owner owner : owners) {
            ownerMapper.create(owner);
        }
        System.out.println("owners = " + owners);
        Assert.assertEquals(4, ownerMapper.findAll().size());
    }

    @Test
    public void test04_createOwnerWithPetsAndFindById() throws InterruptedException {
        List<Owner> ownersWithPets = Arrays.asList(new Owner("Алексей").addPet(new Pet("Ася")).addPet(new Pet("Мотя")));

        for (Owner owner : ownersWithPets) {
            ownerMapper.create(owner);
            Owner savedOwner = ownerMapper.findOneByName("Алексей");
            System.out.println("savedOwner = " + savedOwner);
            for (Pet pet : owner.getPets()) {
                pet.setOwner_id(savedOwner.getId());
                petMapper.create(pet);
                List<Pet> savedPets = petMapper.findAll();
                System.out.println("savedPets = " + savedPets);
            }
        }
        Owner owner = ownerMapper.findOneById(5);
        Assert.assertEquals("Алексей", owner.getFirstName());
        owner.setPets(ownerMapper.selectPets(owner.getId()));
        /*System.out.println("owner = " + owner);*/
        /*Owner owner = ownersWithPetsFromDB.get(4);*/

        Pet pet1 = owner.getPets().get(0);
        Pet pet2 = owner.getPets().get(1);
        Assert.assertEquals("Ася", pet1.getName());
        Assert.assertEquals("Мотя", pet2.getName());
    }


    @Test
    public void test05_findAndUpdate() {
        String name = "Вася";
        Owner owner = ownerMapper.findOneByName(name);
        Assert.assertEquals(name, owner.getFirstName());

        String newName = "Василий";
        owner.setFirstName(newName);
        ownerMapper.save(owner);
        Owner updatedOwner = ownerMapper.findOneByName(newName);
        Assert.assertEquals(newName, updatedOwner.getFirstName());
    }


    @Test
    public void test06_removeAndCount() {
        String name = "Василий";
        Owner owner = ownerMapper.findOneByName(name);
        Assert.assertEquals(name, owner.getFirstName());

        ownerMapper.delete(owner);

        Assert.assertEquals(4, ownerMapper.count().intValue());
    }

    @Test
    public void test07_removeListOfRecords() {
        List<Owner> owners = ownerMapper.findByNames("Петя", "Марина");
        Assert.assertEquals(2, owners.size());
        Assert.assertTrue(owners.stream().anyMatch(owner -> owner.getFirstName().equals("Петя")));
        Assert.assertTrue(owners.stream().anyMatch(owner -> owner.getFirstName().equals("Марина")));

        ownerMapper.deleteAllWhereIdIn(owners.stream().map(Owner::getId).collect(Collectors.toList()));

        Assert.assertEquals(2, ownerMapper.count().intValue());

        petMapper.deleteAll();
        ownerMapper.deleteAll();
    }

}

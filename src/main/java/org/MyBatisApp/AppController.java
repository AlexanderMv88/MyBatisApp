package org.MyBatisApp;

import org.MyBatisApp.entity.Owner;
import org.MyBatisApp.entity.Pet;
import org.MyBatisApp.mapper.OwnerMapper;
import org.MyBatisApp.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class AppController {

    @Autowired
    OwnerMapper ownerMapper;

    @Autowired
    PetMapper petMapper;


    @RequestMapping(value = "init", method=RequestMethod.GET)
    public ResponseEntity<?> createTestRecords(){
        Owner owner = new Owner(1, "Коля");
        owner.addPet(new Pet(1, "Мурзик"));
        owner.addPet(new Pet(2, "Ася"));

        Owner owner1 = new Owner(2, "Дима");
        owner1.addPet(new Pet(3, "Стела"));

        List<Owner> owners = new ArrayList<>();
        owners.add(owner);
        owners.add(owner1);

        for (Owner o:owners) {
            createNewOwnerWithPets(o);
        }

        List<String> names = owners.stream()
                .map(Owner::getFirstName)
                .collect(Collectors.toList());
        System.out.println("names = " + names);

        String[] nameArr = new String[names.size()];
        nameArr = names.toArray(nameArr);
        List<Owner> savedOwners = ownerMapper.findByNames(nameArr);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(savedOwners)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @RequestMapping(value = "clear", method=RequestMethod.GET)
    public ResponseEntity<?> deleteAllTestRecords(){
        ownerMapper.deleteAll();
        petMapper.deleteAll();

        return ResponseEntity.status(202).build();
    }


    @RequestMapping(value = "", method=RequestMethod.OPTIONS)
    public ResponseEntity options(HttpServletResponse response){
        response.setHeader("Allow", "GET,POST,PUT,DELETE,OPTIONS");
        return new ResponseEntity(HttpStatus.OK);
    }


    @RequestMapping(value = "findAllWithPets", method=RequestMethod.GET)
    public List<Owner> findAllWithPets(){
        List<Owner> owners = ownerMapper.findAllWithPets();
        System.out.println("owners = " + owners);
        /*for (Owner owner:owners){
            owner.getPets().size();
        }*/
        System.out.println("owners = " + owners);
        return owners;

    }


    @RequestMapping(value =  "addOwnersWithPets", method=RequestMethod.POST)
    public ResponseEntity<?> createOwnersWithPets(@RequestBody List<Owner> owners){
        if (owners == null) return ResponseEntity.unprocessableEntity().build();
        if (owners.size() == 0) return ResponseEntity.unprocessableEntity().build();

        for (Owner owner:owners) {
            if (owner.getFirstName() == null) return ResponseEntity.unprocessableEntity().build();
            createNewOwnerWithPets(owner);
        }

        List<String> names = owners.stream()
                .map(Owner::getFirstName)
                .collect(Collectors.toList());
        System.out.println("names = " + names);

        String[] nameArr = new String[names.size()];
        nameArr = names.toArray(nameArr);

        List<Owner> savedOwners = ownerMapper.findByNames(nameArr);
        System.out.println("savedOwners = " + savedOwners);
        for (Owner owner:savedOwners) {
            List<Pet> savedPets = ownerMapper.selectPets(owner.getId());
            System.out.println("saved pets = " + savedPets);
            owner.setPets(savedPets);
        }

        if (savedOwners.equals(owners)) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .buildAndExpand(savedOwners)
                    .toUri();

            return ResponseEntity.created(location).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value =  "addOwnerWithPets", method=RequestMethod.POST)
    public ResponseEntity<?> createOwnerWithPets(@RequestBody Owner owner){
        if (owner == null) return ResponseEntity.unprocessableEntity().build();
        if (owner.getFirstName() == null) return ResponseEntity.unprocessableEntity().build();

        createNewOwnerWithPets(owner);

        Owner savedOwner = ownerMapper.findOneById(owner.getId());
        savedOwner.setPets(ownerMapper.selectPets(owner.getId()));
        System.out.println("savedOwner = " + savedOwner);

        if (savedOwner.equals(owner)) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedOwner)
                    .toUri();

            return ResponseEntity.created(location).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value =  "addPet", method=RequestMethod.POST)
    public ResponseEntity<?> createPet(@RequestBody Pet pet){
        if (pet == null) return ResponseEntity.unprocessableEntity().build();

        petMapper.create(pet);

        Pet savedPet = petMapper.findOneById(pet.getId());

        System.out.println("savedPet = " + savedPet);

        if (savedPet.equals(pet)) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(savedPet)
                    .toUri();

            return ResponseEntity.created(location).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value =  "changeOwnerWithPets/{id}", method=RequestMethod.PUT)
    public ResponseEntity<?> changeOwnerWithPets(@PathVariable long id, @RequestBody  Owner owner){
        if (owner == null) return ResponseEntity.unprocessableEntity().build();


        owner.setId(id);
        ownerMapper.save(owner);
        for (Pet pet: owner.getPets()){
            petMapper.save(pet);
        }


        Owner savedOwner = ownerMapper.findOneByName(owner.getFirstName());
        savedOwner.setPets(ownerMapper.selectPets(owner.getId()));
        System.out.println("savedOwner = " + savedOwner);

        if (savedOwner.equals(owner)) {
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .buildAndExpand(savedOwner.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }



    @RequestMapping(value = "/deleteOwnerWithPets/{id}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteOwnerWithPets(@PathVariable long id){
        Owner owner = ownerMapper.findOneById(id);

        if (owner!=null) {
            ownerMapper.delete(owner);
            List<Pet> pets = ownerMapper.selectPets(id);
            petMapper.deleteAllWhereIdIn(pets.stream()
                    .map(Pet::getId)
                    .collect(Collectors.toList()));
            if (ownerMapper.findOneById(id)==null) {
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.unprocessableEntity().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }


    }

    @RequestMapping(value = "/findBy", method=RequestMethod.GET)
    public List<Owner> findByFullName(@RequestParam(value="firstName") String name){
        return ownerMapper.findByNameWithPets(name);
    }

    @RequestMapping(value = "/findAll", method=RequestMethod.GET)
    public List<Owner> findAll(){
        List<Owner> owners = ownerMapper.findAll();
        /*for(Owner owner:owners){
            owner.setPets(ownerMapper.selectPets(owner.getId()));
        }*/

        return owners;
    }

    private void createNewOwnerWithPets(Owner owner) {
        /*long id = */ownerMapper.create(owner);
        //owner.setId(id);
        System.out.println("create owner "+owner);
        if (owner.getPets()!=null) {
            for (Pet pet : owner.getPets()) {
                pet.setOwner_id(owner.getId());
                petMapper.create(pet);
                System.out.println("create pet " + pet.getName());
            }
        }
    }


}

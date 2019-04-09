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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyBatisAppApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class RestTests {
    @Autowired
    private OwnerMapper ownerMapper;
    @Autowired
    private PetMapper petMapper;

    @Autowired
    private MockMvc mvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    List<Owner> owners = new ArrayList<>();

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


    @Test
    public void test00_getAllCommandsUsingOptions() throws Exception {
        MvcResult res = mvc.perform(options("/api/")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String allowCommands = res.getResponse().getHeader("Allow");

        System.out.println("allowCommands = " + allowCommands);
    }


    @Test
    public void test01_createOwnersWithPetsUsingPost() throws Exception {
        Owner owner = new Owner( "Коля");
        owner.addPet(new Pet("Мурзик"));
        owner.addPet(new Pet("Ася"));

        Owner owner1 = new Owner("Дима");
        owner1.addPet(new Pet("Стела"));

        owners.add(owner);
        owners.add(owner1);

        String jsonObj = json(owners);
        mvc.perform(post("/api/addOwnersWithPets")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isCreated());
    }

    @Test
    public void test02_createOwnerWithPetsUsingPost() throws Exception {

        Owner owner = new Owner("Саша");
        owner.addPet(new Pet("Вольт"));

        String jsonObj = json(owner);
        mvc.perform(post("/api/addOwnerWithPets")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isCreated());
    }

    @Test
    public void test03_getAllOwnersWithPetsUsingGet() throws Exception {
        mvc.perform(get("/api/findAll")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName", is("Коля")));
    }

    @Test
    public void test04_getOwnerWithPetsByNameUsingGet() throws Exception {
        mvc.perform(get("/api/findBy?firstName=Дима")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName", is("Дима")));
    }

    @Test
    public void test05_changeOwnerWithPetUsingPut() throws Exception {

        Owner owner = getOwnerWithPets("Дима");

        owner.setFirstName("Димон");
        String jsonObj = json(owner);

        mvc.perform(put("/api/changeOwnerWithPets/"+owner.getId())
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isCreated());

        /*mvc.perform(get("/api/findBy?firstName=Димон")
                .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstName", is("Димон")));*/
    }

    private Owner getOwnerWithPets(String ownerName) {
        Owner owner = ownerMapper.findOneByName(ownerName);
        owner.setPets(ownerMapper.selectPets(owner.getId()));
        return owner;
    }

    @Test
    public void test06_removeOwnerWithPetUsingDelete(){
        Owner owner = getOwnerWithPets("Димон");
        Assert.assertEquals("Димон",owner.getFirstName());
        try {
            mvc.perform(delete("/api/deleteOwnerWithPets/"+ owner.getId())
                    .with(httpBasic("Alexander", "12345"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

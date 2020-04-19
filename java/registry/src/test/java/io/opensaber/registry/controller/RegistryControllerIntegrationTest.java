package io.opensaber.registry.controller;

import com.google.gson.Gson;
import io.opensaber.pojos.Response;
import io.opensaber.pojos.ResponseParams;
import io.opensaber.registry.app.OpenSaberApplication;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OpenSaberApplication.class})
@TestPropertySource(locations = "classpath:application.yml")
@AutoConfigureMockMvc
public class RegistryControllerIntegrationTest{
    @Autowired
    private MockMvc mockMvc;
    private String orgRequestBody;


    @Before
    public void setup() throws IOException {
        orgRequestBody = IOUtils.resourceToString("/requestsbody/OrganizationReqBody.json", Charset.defaultCharset());

    }


    @Test
    public void shouldSaveAValidOrganization() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post("/add")
                        .content(orgRequestBody)
        )
                .andExpect(status().isOk())
                .andReturn();

        Response response = new Gson().fromJson(mvcResult.getResponse().getContentAsString(), Response.class);

        assertEquals(Response.Status.SUCCESSFUL, response.getParams().getStatus());
    }
}
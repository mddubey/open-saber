package io.opensaber.registry.controller;

import com.google.gson.Gson;
import io.opensaber.pojos.Response;
import io.opensaber.registry.app.OpenSaberApplication;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {OpenSaberApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistryControllerIntegrationTest{
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Before
    public void setup() throws IOException {
        testRestTemplate = new TestRestTemplate();
    }


    @Test
    public void shouldSaveAValidOrganization() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/001_ValidOrganization.json");
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.SUCCESSFUL, responseEntity.getBody().getParams().getStatus());
    }

    @Test
    public void shouldFailIfRequestDoesNotHaveOrganization() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/002_Wrong_Resource.json");
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.UNSUCCESSFUL, responseEntity.getBody().getParams().getStatus());
        assertEquals("can't validate, Encounter: schema has a problem!", responseEntity.getBody().getParams().getErrmsg());
    }

    @Test
    public void shouldFailIfOrganizationDoesNotHaveResourceType() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/003_No_Resource_Type.json");
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.UNSUCCESSFUL, responseEntity.getBody().getParams().getStatus());
        assertEquals("#/Organization: required key [resourceType] not found", responseEntity.getBody().getParams().getErrmsg());
    }

    @Test
    public void shouldFailIfOrganizationDoesNotHaveID() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/004_No_ID.json");
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.UNSUCCESSFUL, responseEntity.getBody().getParams().getStatus());
        assertEquals("#/Organization: required key [id] not found", responseEntity.getBody().getParams().getErrmsg());
    }

    @Test
    public void shouldFailIfOrganizationDoesNotHaveName() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/005_No_Name.json");
        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.UNSUCCESSFUL, responseEntity.getBody().getParams().getStatus());
        assertEquals("#/Organization: required key [name] not found", responseEntity.getBody().getParams().getErrmsg());
    }

    @Test
    public void shouldFailIfResourceTypeIsNotOrganization() throws Exception {
        Map payload = payloadFromResourceFile("/requestsbody/003_Wrong_Resource_Type.json");

        ResponseEntity<Response> responseEntity = testRestTemplate.postForEntity(getUrl(), payload, Response.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Response.Status.UNSUCCESSFUL, responseEntity.getBody().getParams().getStatus());
        assertEquals("#/Organization/resourceType: #: only 1 subschema matches out of 2", responseEntity.getBody().getParams().getErrmsg());
    }

    private String getUrl() {
        return "http://localhost:" + port + "/add";
    }

    private Map payloadFromResourceFile(String filename) throws IOException {
        String content = IOUtils.resourceToString(filename, Charset.defaultCharset());
        return new Gson().fromJson(content, Map.class);
    }
}
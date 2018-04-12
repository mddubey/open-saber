package io.opensaber.registry.service.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.Random;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import io.opensaber.registry.config.GenericConfiguration;
import io.opensaber.registry.controller.RegistryTestBase;
import io.opensaber.registry.middleware.util.Constants;
import io.opensaber.registry.schema.config.SchemaConfigurator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EncryptionServiceImpl.class, Environment.class, GenericConfiguration.class, })
@ActiveProfiles(Constants.TEST_ENVIRONMENT)
public class RegistryEncryptionServiceImplTest extends RegistryTestBase {


	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Value("${encryption.uri}")
	private String encryptionUri;

	@Value("${decryption.uri}")
	private String decryptionUri;
	
	@Mock
	private SchemaConfigurator mockSchemaConfigurator;
	
	@Mock
	private RestTemplate mockRestTemplate;
	
	@InjectMocks
	@Autowired
	EncryptionServiceImpl encryptionServiceImpl;
	
	private MockRestServiceServer mockServer;

	@Before
	public void setUp() {
	mockServer = MockRestServiceServer.createServer(mockRestTemplate);
	}
	EncryptionServiceImpl encryptionServiceImplObj= new EncryptionServiceImpl();

	@Test
	public void test_encrypted_value_as_expected() throws Exception {
		String value = "1234567890123456";
		String encryptedValue = encryptionServiceImplObj.encrypt(value);
		assertNotEquals(value, encryptedValue);
		assertNotNull(encryptedValue);
	}

	@Test
	public void test_decrypted_value_as_expected() throws Exception {
		String value = "v1|11|PKCS1|eYIVlw6o/KVl9LhbW+WmQJO3WHU8pUaQa5lRpggBPs/l9TThFA5tNzx2nO0mSlP0sgauSGdR+zEdHDzgIFw2yA==";
		String decryptedValue = encryptionServiceImplObj.decrypt(value);
		assertEquals("1234567890123456", decryptedValue);
	}

	@Test
	public void test_service_encryption() throws Exception {
	
		 byte[] array = new byte[7];
		 new Random().nextBytes(array);
		 String generatedString = new String(array, Charset.forName("UTF-8"));
		 String response = encryptionServiceImplObj.encrypt(generatedString);
		 
		 assertNotEquals(generatedString,response);
		 assertNotEquals(null,response);
		 
	}
	
	@Test
	public void test_null_value_for_decryption() throws Exception {
		 byte[] array = new byte[7];
		 new Random().nextBytes(array);
		 String generatedString = new String(array, Charset.forName("UTF-8"));
		 String encryptedValue = encryptionServiceImplObj.encrypt(generatedString);
		 String decryptedValue = encryptionServiceImplObj.decrypt(encryptedValue);
		 
		 if(decryptedValue==null) {
			 expectedEx.expect(NullPointerException.class);
			 expectedEx.expectMessage(containsString("decrypted value cannot be null!"));
			 }
		 assertNotEquals(null,decryptedValue);
	}
	@Test
	public void test_service_decryption() throws Exception {
		 byte[] array = new byte[7];
		 new Random().nextBytes(array);
		 String generatedString = new String(array, Charset.forName("UTF-8"));
		 String encryptedValue = encryptionServiceImplObj.encrypt(generatedString);
		 String decryptedValue = encryptionServiceImplObj.decrypt(encryptedValue);
		 
		 assertEquals(generatedString,decryptedValue);
		 assertNotEquals(null,decryptedValue);
	}
	@Test
	public void test_null_value_for_encryption() throws Exception {
	
		 byte[] array = new byte[7];
		 new Random().nextBytes(array);
		 String generatedString = new String(array, Charset.forName("UTF-8"));
		 String response = encryptionServiceImplObj.encrypt(generatedString);
		 if(response==null) {
		 expectedEx.expect(NullPointerException.class);
		 expectedEx.expectMessage(containsString("encrypted value cannot be null!"));
		 }
		 assertNotEquals(null,response);		 
	}	    

  }

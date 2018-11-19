package loratb;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.smartcommunitylab.loratb.core.DataManager;
import it.smartcommunitylab.loratb.ext.lora.LoraManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(locations= {"classpath:application.properties","classpath:log4j.properties"})
public class TestLora {
	@Autowired
	private LoraManager loraManager;
	
	@Autowired
	private DataManager dataManager;
	
	@Test
	public void getToken() throws Exception {
		String token = loraManager.getToken();
		assertNotNull(token);
		System.out.println("token:" + token);
	}
	
	@Test
	public void storeApplications() throws Exception {
		dataManager.storeLoraApplications();
	}
	
	@Test
	public void storeDevices() throws Exception {
		dataManager.storeLoraDevices();
	}
	
	
}

package it.smartcommunitylab.loratb.core;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.smartcommunitylab.loratb.exception.HttpException;
import it.smartcommunitylab.loratb.ext.lora.LoraManager;
import it.smartcommunitylab.loratb.ext.tb.ThingsBoardManager;
import it.smartcommunitylab.loratb.model.Application;
import it.smartcommunitylab.loratb.model.Customer;
import it.smartcommunitylab.loratb.model.Device;
import it.smartcommunitylab.loratb.model.User;
import it.smartcommunitylab.loratb.repository.ApplicationRepository;
import it.smartcommunitylab.loratb.repository.CustomerRepository;
import it.smartcommunitylab.loratb.repository.DeviceRepository;
import it.smartcommunitylab.loratb.repository.UserRepository;
import it.smartcommunitylab.loratb.utils.Utils;

@Component
public class DataManager implements MqttMessageListener {
	private static final transient Logger logger = LoggerFactory.getLogger(DataManager.class);
	
	@Value("${lora.mqtt.threads}")
	private int threads;
			
	@Autowired
	private ThingsBoardManager tbManager;
	
	@Autowired
	private LoraManager loraManager;
	
	@Autowired
	private MqttManager mqttManager;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private ObjectMapper mapper = null;
	
	private ExecutorService executor;
	
	@PostConstruct
	public void init() throws Exception {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		BasicThreadFactory factory = new BasicThreadFactory.Builder()
				.namingPattern("mqqt-msg-thread-%d")
				.daemon(true)
				.build();
		executor = Executors.newFixedThreadPool(threads, factory);
		
		mqttManager.setMessageListener(this);
		mqttManager.init();
	}
	
	@Override
	public void onMessage(final String topic, final MqttMessage message) {
		executor.execute(() -> sendTelemetry(message));
	}
	
	private void sendTelemetry(MqttMessage message) {
		try {
			String payload = message.toString();
			JsonNode rootNode = mapper.readTree(payload);
			String devEUI = rootNode.get("devEUI").asText();
			String appId = rootNode.get("applicationID").asText();
			if(rootNode.hasNonNull("object")) {
				Device device = deviceRepository.findByLoraDevEUI(appId, devEUI);
				if(device == null) {
					if(logger.isInfoEnabled()) {
						logger.info(String.format("sendTelemetry - device not found: %s / %s", appId, devEUI));
					}
					return;
				}
				// check if exists in tb
				if(Utils.isNotEmpty(device.getTbTenantId()) &&
						Utils.isNotEmpty(device.getTbId())) {
					//send telemetry
					long timestamp = rootNode.get("timestamp").asLong();
					JsonNode objectNode = convertUplink(rootNode);
					tbManager.sendTelemetry(device, objectNode, timestamp);
					//TODO move log to debug level
					if(logger.isInfoEnabled()) {
						logger.info(String.format("sendTelemetry - sent data to device: %s / %s", appId, devEUI));
					}				
				} else {
					if(logger.isInfoEnabled()) {
						logger.info(String.format("sendTelemetry - device not connected to TB: %s / %s", appId, devEUI));
					}
				}
			} else {
				if(logger.isInfoEnabled()) {
					logger.info(String.format("sendTelemetry - object field not found: %s / %s", appId, devEUI));
				}
			}
		} catch (Exception e) {
			if(logger.isInfoEnabled()) {
				logger.info(String.format("sendTelemetry exception:%s", e.getMessage()));
			}
		}
	}
	
	private JsonNode convertUplink(JsonNode rootNode) {
		ObjectNode uplinkNode = mapper.createObjectNode();
		uplinkNode.put("applicationID", rootNode.get("applicationID").asText());
		uplinkNode.put("applicationName", rootNode.get("applicationName").asText());
		uplinkNode.put("deviceName", rootNode.get("deviceName").asText());
		uplinkNode.put("devEUI", rootNode.get("devEUI").asText());
		uplinkNode.put("adr", rootNode.get("adr").asBoolean());
		uplinkNode.put("fCnt", rootNode.get("fCnt").asInt());
		uplinkNode.put("fPort", rootNode.get("fPort").asInt());
		if(rootNode.hasNonNull("rxInfo")) {
			int count = 0;
			for(final JsonNode objNode : rootNode.get("rxInfo")) {
				String fieldGatewayID = "rxInfo_" + count + "_gatewayID";
				String fieldName = "rxInfo_" + count + "_name";
				String fieldTime = "rxInfo_" + count + "_time";
				String fieldRssi = "rxInfo_" + count + "_rssi";
				String fieldLoRaSNR = "rxInfo_" + count + "_loRaSNR";
				String fieldLatitude = "rxInfo_" + count + "_location_latitude";
				String fieldLongitude = "rxInfo_" + count + "_location_longitude";
				String fieldAltitude = "rxInfo_" + count + "_location_altitude";
				if(objNode.hasNonNull("gatewayID")) {
					uplinkNode.put(fieldGatewayID, objNode.get("gatewayID").asText());
				}
				if(objNode.hasNonNull("name")) {
					uplinkNode.put(fieldName, objNode.get("name").asText());
				}
				if(objNode.hasNonNull("time")) {
					uplinkNode.put(fieldTime, objNode.get("time").asText());
				}
				if(objNode.hasNonNull("rssi")) {
					uplinkNode.put(fieldRssi, objNode.get("rssi").asInt());
				}
				if(objNode.hasNonNull("loRaSNR")) {
					uplinkNode.put(fieldLoRaSNR, objNode.get("loRaSNR").asInt());
				}
				if(objNode.hasNonNull("location")) {
					uplinkNode.put(fieldLatitude, objNode.get("location").get("latitude").asDouble());
					uplinkNode.put(fieldLongitude, objNode.get("location").get("longitude").asDouble());
					uplinkNode.put(fieldAltitude, objNode.get("location").get("altitude").asDouble());
				}
			}
		}
		if(rootNode.hasNonNull("txInfo")) {
			uplinkNode.put("txInfo_frequency", rootNode.get("txInfo").get("frequency").asLong());
			uplinkNode.put("txInfo_dr", rootNode.get("txInfo").get("dr").asInt());
		}
		JsonNode objectNode = rootNode.get("object");
		Iterator<String> fieldNames = objectNode.fieldNames();
		while (fieldNames.hasNext()) {
			String filedName = fieldNames.next();
			JsonNode jsonNode = objectNode.get(filedName);
			if(jsonNode.isTextual()) {
				uplinkNode.put("object_" + filedName, objectNode.get(filedName).asText());
			} else if(jsonNode.isBoolean()) {
				uplinkNode.put("object_" + filedName, objectNode.get(filedName).asBoolean());
			} else if(jsonNode.isDouble() || jsonNode.isFloat()) {
				uplinkNode.put("object_" + filedName, objectNode.get(filedName).asDouble());
			} else if(jsonNode.canConvertToInt()) {
				uplinkNode.put("object_" + filedName, objectNode.get(filedName).asInt());
			} else if(jsonNode.canConvertToLong()) {
				uplinkNode.put("object_" + filedName, objectNode.get(filedName).asLong());
			}
		}
		return uplinkNode;
	}

	private String getTbTenantId() {
		List<User> list = userRepository.findAll();
		if(list.size() > 0) {
			return list.get(0).getTbTenantId();
		}
		return null;
	}
	
	public void storeTbUser() throws Exception {
		User user = tbManager.getUser();
		Optional<User> userOpt = userRepository.findById(user.getTbId());
		if(userOpt.isPresent()) {
			User userDb = userOpt.get();
			userDb.setTbTenantId(user.getTbTenantId());
			userDb.setTbEmail(user.getTbEmail());
			userDb.setTbName(user.getTbName());
			userRepository.save(userDb);
		} else {
			userRepository.deleteAll();
			userRepository.save(user);
		}
	}
	
	public void storeTbCustomers() throws Exception {
		List<Customer> customers = tbManager.getCustomers();
		for (Customer customer : customers) {
			Optional<Customer> optional = customerRepository.findById(customer.getId());
			if(!optional.isPresent()) {
				customerRepository.save(customer);
			}
		}
	}
	
	public void storeTbDevices() throws Exception {
		List<Customer> customers = customerRepository.findAll();
		for(Customer customer : customers) {
			List<Device> devices = tbManager.getDevicesByCustomer(customer.getId());
			for (Device device : devices) {
				Device deviceDb = deviceRepository.findByTbId(device.getTbTenantId(), device.getTbId());
				if(deviceDb == null) {
					deviceRepository.save(device);
				} else {
					deviceDb.setName(device.getName());
					deviceDb.setType(device.getType());
					deviceDb.setTbCredentialsId(device.getTbCredentialsId());
					deviceDb.setTbCredentialsType(device.getTbCredentialsType());
					deviceRepository.save(deviceDb);
				}
			}
		}
	}
	
	public void storeLoraApplications() throws Exception {
		List<Application> applications = loraManager.getApplications();
		for (Application application : applications) {
			Application applicationDb = applicationRepository.findByAppId(application.getAppId());
			if(applicationDb == null) {
				applicationRepository.save(application);
			}
		}
	}
	
	public void storeLoraDevices() throws Exception {
		List<Application> applications = applicationRepository.findAll();
		for (Application application : applications) {
			try {
				List<Device> devices = loraManager.getDevicesByApp(application.getAppId());
				for (Device device : devices) {
					Device deviceDb = deviceRepository.findByLoraDevEUI(device.getLoraApplicationId(), 
							device.getLoraDevEUI());
					if(deviceDb == null) {
						device.setLoraApplicationName(application.getName());
						device.setType(application.getName());
						deviceRepository.save(device);
					} else {
						deviceDb.setName(device.getName());
						deviceDb.setType(application.getName());
						deviceDb.setLoraApplicationName(application.getName());
						deviceDb.setLoraProfileId(device.getLoraProfileId());
						deviceDb.setLoraProfileName(device.getLoraProfileName());
						deviceDb.setLoraStatusBattery(device.getLoraStatusBattery());
						deviceRepository.save(deviceDb);
					}
				}				
			} catch (Exception e) {
				logger.error(String.format("storeLoraDevices exception:%s", e.getMessage()));
			}
		}
	}
	
	public void alignLoraDevices() throws Exception {
		List<Device> devices = deviceRepository.findAll();
		for (Device device : devices) {
			// check lora device
			if(Utils.isNotEmpty(device.getLoraApplicationId()) &&
					Utils.isNotEmpty(device.getLoraDevEUI())) {
				// check if exists in tb
				if(Utils.isNotEmpty(device.getTbTenantId()) &&
						Utils.isNotEmpty(device.getTbId())) {
					try {
						// get existing TB device
						Device tbDevice = tbManager.getDeviceById(device.getTbId());
						// update local fields
						device.setTbId(tbDevice.getTbId());
						device.setTbTenantId(tbDevice.getTbTenantId());
						device.setTbCredentialsId(tbDevice.getTbCredentialsId());
						device.setTbCredentialsType(tbDevice.getTbCredentialsType());
						deviceRepository.save(device);
					} catch (HttpException e) {
						// device not found
						if(e.getResponseCode() == 404) {
							// create a new device in TB
							Device tbDevice = tbManager.addDevice(getTbTenantId(), device);
							device.setTbId(tbDevice.getTbId());
							device.setTbTenantId(tbDevice.getTbTenantId());
							device.setTbCredentialsId(tbDevice.getTbCredentialsId());
							device.setTbCredentialsType(tbDevice.getTbCredentialsType());
							deviceRepository.save(device);
						}
					} catch (Exception e) {
						logger.error(String.format("alignLoraDevices exception:%s", e.getMessage()));
					}
				} else {
					// create a new device in TB
					try {
						Device tbDevice = tbManager.addDevice(getTbTenantId(), device);
						device.setTbId(tbDevice.getTbId());
						device.setTbTenantId(tbDevice.getTbTenantId());
						device.setTbCredentialsId(tbDevice.getTbCredentialsId());
						device.setTbCredentialsType(tbDevice.getTbCredentialsType());
						deviceRepository.save(device);
					} catch (Exception e) {
						logger.error(String.format("alignLoraDevices exception:%s", e.getMessage()));
					}
				}
			}
		}
	}
	
	@Scheduled(cron = "${cronexp}")
	public void refreshLoraDevices() {
		if(logger.isInfoEnabled()) {
			logger.info("refreshLoraDevices started");
		}
		try {
			storeLoraApplications();
			storeLoraDevices();
			alignLoraDevices();
		} catch (Exception e) {
			logger.error(String.format("refreshLoraDevices exception:%s", e.getMessage()));
		}
	}

}

package com.pg.app.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;

import com.pg.app.AppConfig;

/***************************************************************
 * @see http://www.baeldung.com/spring-kafka for a good tutorial and https://kafka.apache.org/documentation/
 * @author pgobin
 *
 *         Demonstrate the usage of a Kafka producer and also the usage of adding a scheduler to you class. It also demonstrated how to use beans as a simple and elegant way to add
 *         this into the application context and use be able to use the @Autwire annotation
 */
@Configuration
public class KafkaProducerConfig {
	private static final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);

	@Value("${kafka.servers}")
	private String kafkaServers;

	@Value("${groupId:p2p-presence}")
	String groupId;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	private static AtomicLong atomicLong = new AtomicLong();

	/*******************************************************
	 * 
	 * @return
	 */
	@Bean
	public ProducerFactory<String, String> producerFactory()
	{
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return new DefaultKafkaProducerFactory(configProps);
	}

	/*****************************************************
	 * 
	 * @return
	 */
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate()
	{
		return new KafkaTemplate<>(producerFactory());
	}

	/*****************************************************
	 * 
	 * @param msg
	 * @return
	 */
	public ListenableFuture<SendResult<String, String>> sendMessage(String msg)
	{
		boolean iproducer = StringUtils.startsWithIgnoreCase(AppConfig.getEnv().getProperty("is-producer", "false"), "true");
		if (iproducer)
		{
			long count = atomicLong.incrementAndGet();
			msg = "[" + count + "] " + msg;
			System.out.println("sending message=>" + msg);

			ListenableFuture<SendResult<String, String>> r = kafkaTemplate.send("paul", msg);
			return r;
		}
		return null;
	}

	/*****************************************************
	 * Demonstrate the use of a scheduler to publish a message to Kafka every fixedRateString value
	 * 
	 * Uncomment the //@Scheduled to enable it.
	 * 
	 * @return
	 */
	@Scheduled(fixedRateString = "10000", initialDelay = 10000)
	public ListenableFuture<SendResult<String, String>> sendMessage()
	{
		String message = "[" + AppConfig.getEnv().getProperty("spring.application.name", "APP") + "] hello";
		return sendMessage(message);
	}

}
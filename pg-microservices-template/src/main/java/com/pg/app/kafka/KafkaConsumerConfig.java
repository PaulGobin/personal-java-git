package com.pg.app.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/***************************************************************
 * @see http://www.baeldung.com/spring-kafka for a good tutorial and https://kafka.apache.org/documentation/
 * @author pgobin
 *
 *         Demonstrate the usage of a Kafka consumer, also demonstrated how to use beans as a simple and elegant way to add this into the application context and use be able to use
 *         the @Autwire annotation
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

	@Value("${kafka.servers}")
	private String kafkaServers;

	/**************
	 * If all the consumer instances have the <b>same</b> consumer group, then the records will effectively be load balanced over the consumer instances. This is a Peer to Peer
	 * mechanism
	 * 
	 * If all the consumer instances have <b>different</b> consumer groups, then each record will be broadcast to all the consumer processes. This is a Pub/Sub mechanism
	 */
	@Value("${groupId:p2p-presence}")
	String groupId;

	public KafkaConsumerConfig()
	{
		// TODO Auto-generated constructor stub
	}

	/*************************************************
	 * 
	 * @return
	 */
	@Bean
	public ConsumerFactory<String, String> consumerFactory()
	{
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
		return new DefaultKafkaConsumerFactory(props);
	}

	/************************************************
	 * 
	 * @return
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory()
	{
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	/**************************************************************************
	 * 
	 * @param message
	 */
	@KafkaListener(topics = "paul", group = "paul-group")
	public void listen(String message)
	{
		// You should perform async-processing here to process the message in a separate Thread Executor.
		// log.info("<<< Kafla consumer received message on groupid paul-group via topic paul. [" + message + "]");
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			log.error(" Error processing message", e);
		}
		log.debug("Received <<= " + message);
	}
}

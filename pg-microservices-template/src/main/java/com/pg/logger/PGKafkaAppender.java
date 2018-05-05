/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.pg.logger;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.mom.kafka.KafkaManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.SerializedLayout;
import org.apache.logging.log4j.core.util.StringEncoder;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * @author pgobin
 * 
 *         This is a custom LogAppender used for sending logs to
 *         Kafka=>LogStash=>ElasticSearch=>Kibana (Grafana etc) Sends log events
 *         to an Apache Kafka topic.
 * 
 *         PG - See kafkaAppender for original I created a Spring Boot
 *         specialized kafkaAppender where we can ready values from
 *         application.properties or config server
 */

@Plugin(name = "PGKafkaAppender", category = Node.CATEGORY, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class PGKafkaAppender extends AbstractAppender {

	private static final ExecutorService executorService = new ThreadPoolExecutor(5, 10, 5000L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<Runnable>(10000));

	private static Runtime _runtime = Runtime.getRuntime();
	private static Property[] _sendToKafkaProperties;

	@PluginFactory
	public static PGKafkaAppender createAppender(@PluginElement("Layout") final Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter,
			@Required(message = "No name provided for KafkaAppender") @PluginAttribute("name") final String name,
			@PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) final boolean ignoreExceptions,
			@Required(message = "No topic provided for KafkaAppender") @PluginAttribute("topic") final String topic,
			@PluginElement("Properties") final Property[] properties,
			@PluginConfiguration final Configuration configuration) {
		// final KafkaManager kafkaManager = new
		// KafkaManager(configuration.getLoggerContext(), name, topic, true, properties,
		// null);
		final KafkaManager kafkaManager = new KafkaManager(configuration.getLoggerContext(), name, topic, properties);

		return new PGKafkaAppender(name, layout, filter, ignoreExceptions, kafkaManager, properties);
	}

	private final KafkaManager manager;

	private PGKafkaAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter,
			final boolean ignoreExceptions, final KafkaManager manager, final Property[] properties) {
		super(name, filter, layout, ignoreExceptions);
		this.manager = manager;
		_sendToKafkaProperties = properties;
	}

	@Override
	public void append(final LogEvent event) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				if (event.getLoggerName().startsWith("org.apache.kafka")) {
					LOGGER.warn("Recursive logging from [{}] for appender [{}].", event.getLoggerName(), getName());
				} else {
					try {
						final Layout<? extends Serializable> layout = getLayout();
						byte[] data;
						if (layout != null) {
							if (layout instanceof SerializedLayout) {
								final byte[] header = layout.getHeader();
								final byte[] body = layout.toByteArray(event);
								data = new byte[header.length + body.length];
								System.arraycopy(header, 0, data, 0, header.length);
								System.arraycopy(body, 0, data, header.length, body.length);
							} else {
								data = layout.toByteArray(event);
							}
						} else {
							data = StringEncoder.toBytes(event.getMessage().getFormattedMessage(),
									StandardCharsets.UTF_8);
						}
						// manager.send(data);
						// Paul commented out the above line and replace it with the below, Add custom
						// json data

						String s = new String(data);
						com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

						Map<String, Object> map = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {
						});

						setSend2KafkaPropertiesInfo(map);
						String dataToSend = objectMapper.writeValueAsString(map);
						// System.out.println(dataToSend);
						byte[] b = dataToSend.getBytes();
						manager.send(b);
					} catch (final Exception e) {
						LOGGER.error("Unable to write to Kafka [{}] for appender [{}].", manager.getName(), getName(),
								e);
						throw new AppenderLoggingException("Unable to write to Kafka in appender: " + e.getMessage(),
								e);
					}
				}
			}
		});
	}

	@Override
	public void start() {
		super.start();
		manager.startup();
	}

	@Override
	public boolean stop(final long timeout, final TimeUnit timeUnit) {
		setStopping();
		boolean stopped = super.stop(timeout, timeUnit, false);
		stopped &= manager.stop(timeout, timeUnit);
		setStopped();
		return stopped;
	}

	@Override
	public String toString() {
		return "KafkaAppender{" + "name=" + getName() + ", state=" + getState() + ", topic=" + manager.getTopic() + '}';
	}

	private void setSend2KafkaPropertiesInfo(Map<String, Object> map) {
		if (_sendToKafkaProperties != null) {
			for (Property property : _sendToKafkaProperties) {
				String pname = property.getName();
				if (StringUtils.startsWithIgnoreCase(pname, "send2kafka.")) {
					if (property.isValueNeedsLookup()) {
						setLookupData(property, map);
					} else {
						String name = StringUtils.substringAfter(pname, "send2kafka.");
						String value = property.getValue();
						map.put(name, value);
					}
				}
			}
		}

	}

	/*********************************************
	 * 
	 * @param property
	 * @param map
	 */
	private void setLookupData(final Property property, Map<String, Object> map) {
		final String systemPropertyDirective = "${SystemProperty.";
		final String runtimePropertyDirective = "${OperatingSystemProperty.";
		final String environmentPropertyDirective = "${EnvironmentProperty.";
		String pname = property.getName();
		String value = StringUtils.trim(property.getValue());
		boolean lookupNeeded = property.isValueNeedsLookup();
		if (lookupNeeded) {
			String name = StringUtils.substringAfter(pname, "send2kafka.");
			if (StringUtils.containsIgnoreCase(value, systemPropertyDirective)) {
				String sysPredicate = StringUtils.substringBetween(value, systemPropertyDirective, "}");
				String sysPropValue = System.getProperty(sysPredicate);
				if (StringUtils.isEmpty(sysPredicate) || StringUtils.isEmpty(sysPropValue)) {
					return;
				}
				map.put(name, sysPropValue);
			} else if (StringUtils.containsIgnoreCase(value, runtimePropertyDirective)) {
				setOperatingSystemProperty(name, StringUtils.substringBetween(value, runtimePropertyDirective, "}"),
						map);
			} else if (StringUtils.containsIgnoreCase(value, environmentPropertyDirective)) {
				setEnvironmentProperty(name, StringUtils.substringBetween(value, environmentPropertyDirective, "}"),
						map);
			}

		}
		// if (LOGGER.isDebugEnabled())
		/*
		 * { NumberFormat format = NumberFormat.getInstance(); StringBuilder sb = new
		 * StringBuilder(); long maxMemory = _runtime.maxMemory(); long allocatedMemory
		 * = _runtime.totalMemory(); long freeMemory = _runtime.freeMemory();
		 * map.put("Free memory", format.format(freeMemory / 1024));
		 * map.put("Allocated memory", format.format(allocatedMemory / 1024));
		 * map.put("Max memory", format.format(maxMemory / 1024));
		 * map.put("Total free memory", format.format((freeMemory + (maxMemory -
		 * allocatedMemory)) / 1024));
		 * 
		 * }
		 */
	}

	private void setOperatingSystemProperty(String kafkaName, String runtimeLookupPredicate, Map<String, Object> map) {
		if (StringUtils.isEmpty(kafkaName) || StringUtils.isEmpty(runtimeLookupPredicate)) {
			return;
		}
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		if (StringUtils.equalsIgnoreCase(runtimeLookupPredicate, "HostName")) {
			try {
				map.put(kafkaName, InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				// no-op
			}
		} else if (StringUtils.equalsIgnoreCase(runtimeLookupPredicate, "os.name")) {
			String osName = osBean.getName();
			map.put(kafkaName, osName);
		} else if (StringUtils.equalsIgnoreCase(runtimeLookupPredicate, "os.freeMemory")) {
			long freeMemory = _runtime.freeMemory();
			map.put(kafkaName, freeMemory);
		}
	}

	private void setEnvironmentProperty(String kafkaName, String environmentPropertyKey, Map<String, Object> map) {
		if (StringUtils.isEmpty(kafkaName) || StringUtils.isEmpty(environmentPropertyKey)) {
			return;
		}
		map.put(kafkaName, System.getenv(environmentPropertyKey));

	}

}

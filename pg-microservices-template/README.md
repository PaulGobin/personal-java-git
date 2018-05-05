# bvn-microservices-template
This template allows you to easily create microservices using spring boot and Netflix OSS stack. It removed all the boiler plate code used to connect and register with Eurek Service Registry, API Gateway using Zuul Proxy, Distribute Config Service, KELK (Kafka, Elasticsearch, Logstash and Kibana) stack. Additionally, it has a Kafka Appender, so you can do regular log.xxx() seamlesly, It also has Docker file and docker build goal. By using this template, you will be able to stand up a microservice is less than 5 mins. It also include JPA templates for RDBM and Mongo, along with examples of using Kafka consumer and producer.  @TODO- add Zipkin, Hystrix Circuit breaker, Prometeous and Grafana, and Chaos Monkey.

Add hooks for Feing client side load balancing, this allows us to call other service endpoints via our registry service while providing client side load balancing. You don't need to use the host name when calling the remote service but use the service name instead.
Example: http://SERVICE-NAME/abc/v1/getData

NOTE the use of SERVICE-NAME and not a host name for this example.

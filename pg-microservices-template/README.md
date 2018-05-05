# bvn-microservices-template


Add hooks for Feing client side load balancing, this allows us to call other service endpoints via our registry service while providing client side load balancing. You don't need to use the host name when calling the remote service but use the service name instead.
Example: http://recent-activity-service/recent-activity/v1/onRecentVoiceMessageReceived

NOTE the use of recent-activity-service and not a host name for this POST example.

/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme.pulsar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.acme.pulsar.model.CustomerSignup;
import com.acme.pulsar.model.UserSignup;
import com.acme.pulsar.model.SignupTier;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.common.io.SinkConfig;
import org.apache.pulsar.common.io.SourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.pulsar.annotation.PulsarListener;
import org.springframework.pulsar.function.PulsarSink;
import org.springframework.pulsar.function.PulsarSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static org.springframework.pulsar.function.PulsarFunctionOperations.FunctionStopPolicy.DELETE;

@SpringBootApplication
public class SignupApplication {

	private static final Logger LOG = LoggerFactory.getLogger(SignupApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SignupApplication.class, args);
	}

	@Bean
	PulsarSource userSignupRabbitSource() {
		var configs = new HashMap<String, Object>();
		configs.put("host", "rabbitmq");
		configs.put("port", 5672);
		configs.put("virtualHost", "/");
		configs.put("username", "guest");
		configs.put("password", "guest");
		configs.put("queueName", "user_signup_queue");
		configs.put("connectionName", "user_signup_pulsar_source");
		SourceConfig sourceConfig = SourceConfig.builder()
				.tenant("public")
				.namespace("default")
				.name("UserSignupRabbitSource")
				.archive("builtin://rabbitmq")
				.topicName("user-signup-topic")
				.configs(configs)
				.build();
		return new PulsarSource(sourceConfig, DELETE, null);
	}

	@PulsarListener(topics = "user-signup-topic", subscriptionType = SubscriptionType.Shared)
	void logIncomingUserSignups(UserSignup signup) {
		LOG.info("FROM PULSAR user-signup-topic ==> {}", signup);
	}

	@Bean
	public Function<UserSignup, CustomerSignup> customerSignupProcessor() {
		return (user) -> {
			var tier = user.signupTier();
			// Log and ignore non-enterprise users
			if (tier != SignupTier.ENTERPRISE) {
				LOG.info("DROP non-enterprise user ==> {}", user);
				return null;
			}
			// Create customer signup for enterprise users
			var customer = CustomerSignup.from(user);
			LOG.info("TO PULSAR customer-signup-topic ==> {}", customer);
			return customer;
		};
	}

	@PulsarListener(topics = "customer-signup-topic", subscriptionType = SubscriptionType.Shared)
	void logCustomerSignups(CustomerSignup customer) {
		LOG.info("FROM PULSAR customer-signup-topic => {}", customer);
	}

	@Bean
	PulsarSink customerSignupHttpSink(@Value("${slack.webhook-url}") String slackWebhookUrl) {
		Assert.state(StringUtils.hasText(slackWebhookUrl), "You must set the 'slack.webhook-url' property");
		Map<String, Object> configs = Map.of("url", slackWebhookUrl);
		var sinkConfig = SinkConfig.builder()
				.tenant("public")
				.namespace("default")
				.name("CustomerSignupHttpSink")
				.archive("builtin://http")
				.inputs(List.of("customer-signup-topic"))
				.configs(configs).build();
		return new PulsarSink(sinkConfig, DELETE, null);
	}

}

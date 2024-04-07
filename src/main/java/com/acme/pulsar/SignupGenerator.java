package com.acme.pulsar;

import java.util.Random;

import com.acme.pulsar.model.UserSignup;
import com.acme.pulsar.model.SignupTier;
import com.devskiller.jfairy.Fairy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
class SignupGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(SignupGenerator.class);
	private static final Random random = new Random();
	private static final Fairy fairy = Fairy.create();

	@Autowired
	private RabbitTemplate rabbit;

	@Scheduled(initialDelay = 5_000, fixedDelay = 5_000)
	void produceSignupToRabbit() {
		var signup = this.generate();
		this.rabbit.convertAndSend("user_signup_queue", signup);
		LOG.info("TO RABBIT user_signup_queue => {}", signup);
	}

	private UserSignup generate() {
		var person = fairy.person();
		var tier = SignupTier.values()[random.nextInt(SignupTier.values().length)];
		return new UserSignup(tier, person.getFirstName(), person.getLastName(), person.getCompanyEmail(),
				System.currentTimeMillis());
	}

	@Configuration(proxyBeanMethods = false)
	static class SignupGeneratorConfig {
		@Bean
		Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
			return new Jackson2JsonMessageConverter();
		}
	}
}

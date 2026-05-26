package com.traceability.solicitudes.infrastructure.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mail.dsl.Mail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class NotificationIntegrationConfig {

    @Bean
    @Profile("jpa")
    public MessageChannel smtpOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @Profile("jpa")
    public IntegrationFlow smtpOutboundFlow(MessageChannel smtpOutboundChannel, JavaMailSender mailSender) {
        return IntegrationFlow.from(smtpOutboundChannel)
                .handle(Mail.outboundAdapter(mailSender), spec -> spec.id("smtpOutboundHandler"))
                .get();
    }
}

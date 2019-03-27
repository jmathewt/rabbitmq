package com.jthomas.rabbitmq.publish;

import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Publisher {
  
  @Autowired
  private RabbitTemplate rabbitTemplate;
  
  @Autowired
  private PublishConfirmationListener publishConfirmationListener;
  
  public void sendMessage(String testMessage) throws AmqpException, Exception {
    MessageProperties messageProperties = new MessageProperties();
    messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
    Message message = new Message(testMessage.getBytes(), messageProperties);
    
    CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
    
    publishConfirmationListener.getUnConfirmedMessages().put(correlationData.getId(), false);
    
    System.out.println("\nPublisher sending message with id - " + correlationData.getId());
    rabbitTemplate.convertAndSend("wrong-exchange", "wrong-queue", message, correlationData);
    
  }

}

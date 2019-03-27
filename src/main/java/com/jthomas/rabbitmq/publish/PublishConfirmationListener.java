package com.jthomas.rabbitmq.publish;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

@Component
public class PublishConfirmationListener implements ConfirmCallback{
  
  Map<String, Boolean> unConfirmedMessages = new ConcurrentHashMap<>(); 

  @Override
  public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    
    System.out.println("\n\nCalling confirm callback for status of the published message with id - " + correlationData.getId());
    System.out.println("Acknowledge - " + ack);
    System.out.println("Cause - " + cause);
    System.out.println("\n\n");
    
    if(ack == true) {
      unConfirmedMessages.remove(correlationData.getId());
    }else {
      // you can add logic to handle failures
    }
  }

  public Map<String, Boolean> getUnConfirmedMessages() {
    return unConfirmedMessages;
  }

  public void setUnConfirmedMessages(Map<String, Boolean> confirmedMessages) {
    this.unConfirmedMessages = confirmedMessages;
  }

}

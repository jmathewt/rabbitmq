package com.jthomas.rabbitmq.publish;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
import org.springframework.stereotype.Component;

@Component
public class ReturnCallbackListener implements ReturnCallback{

  @Override
  public void returnedMessage(Message message, int replyCode, String replyText, String exchange,
      String routingKey) {
    System.out.println("\n\nReturned message - ");
    System.out.println("Message Body - " + new String(message.getBody()));
    System.out.println("Reply Code - " + replyCode);
    System.out.println("Reply Text - " + replyText);
    System.out.println("Exchange - " + exchange);
    System.out.println("Routing Key - " + routingKey);
    System.out.println("\n\n");
  }

}

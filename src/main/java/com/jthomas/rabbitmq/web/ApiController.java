package com.jthomas.rabbitmq.web;

import java.util.Map;

import org.springframework.amqp.AmqpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jthomas.rabbitmq.publish.PublishConfirmationListener;
import com.jthomas.rabbitmq.publish.Publisher;

@RestController
@RequestMapping("/test-rabbitmq")
public class ApiController {
  
  @Autowired
  private Publisher publisher;
  
  @Autowired
  private PublishConfirmationListener publishConfirmationListener;
  
  @RequestMapping("/publish")
  public void invokePublisher() throws AmqpException, Exception {
    System.out.println("Invoking Publisher..........");
    try {
      for(int i=0; i<1; i++) {
        publisher.sendMessage("test-message - " + i);
      }
    }finally {
      Map<String, Boolean> unConfirmedMessages = publishConfirmationListener.getUnConfirmedMessages();
      Thread.sleep(10000);
      if(unConfirmedMessages.size() > 0) {
        System.out.println("Unconfirmed messages - " + unConfirmedMessages);
      }
    }
    
  }

}

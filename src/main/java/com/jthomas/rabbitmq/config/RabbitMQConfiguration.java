package com.jthomas.rabbitmq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.jthomas.rabbitmq.publish.PublishConfirmationListener;
import com.jthomas.rabbitmq.publish.ReturnCallbackListener;

@Configuration
public class RabbitMQConfiguration {
  
  @Autowired
  private PublishConfirmationListener confirmationListener;
  
  @Autowired
  private ReturnCallbackListener returnCallbackListener;
  
  @Bean
  public ConnectionFactory connectionFactory() throws Exception{
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost("localhost");
    connectionFactory.setPort(5672);
    connectionFactory.setVirtualHost("/ofo");
    connectionFactory.setUsername("ofo");
    connectionFactory.setPassword("ofo");
    connectionFactory.setRequestedHeartBeat(10);
    connectionFactory.setChannelCacheSize(1000);
    connectionFactory.setPublisherConfirms(true);
    connectionFactory.setPublisherReturns(true);
    return connectionFactory;
  }
  
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    RetryTemplate retryTemplate = new RetryTemplate();
    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(10000);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
    retryPolicy.setMaxAttempts(10);
    retryTemplate.setRetryPolicy(retryPolicy);
    rabbitTemplate.setRetryTemplate(retryTemplate);
    rabbitTemplate.setConfirmCallback(confirmationListener);
    rabbitTemplate.setReturnCallback(returnCallbackListener);
    rabbitTemplate.setMandatory(true);
    
    return rabbitTemplate;
  }
  
  @Bean
  public Queue testQueue(){
    Map<String, Object> args = new HashMap<>();
    args.put("x-max-priority", 10);
    return new Queue("test-queue", true, false, false, args);
  }
  
  @Bean
  public Queue testDeadLetterQueue(){
    Map<String, Object> args = new HashMap<String, Object>();
    args.put("x-dead-letter-exchange", "");
    args.put("x-dead-letter-routing-key", "test-queue");
    args.put("x-message-ttl", 10000);
    return new Queue("test-deadletter-queue", true, false, false, args);
  }
  
  @Bean
  public Exchange testXChange(){
    return new DirectExchange("test-exchange", true, false, null);
  }
  
 /* @Bean
  public List<Declarable> testExchangeAndQueueBindings(Exchange testXChange, Queue testQueue) {
    FanoutExchange nlsExchange = (FanoutExchange) testXChange;
    return Arrays.<Declarable>asList(
        testQueue,
        nlsExchange,
        BindingBuilder.bind(testQueue).to(nlsExchange));
  }*/

}

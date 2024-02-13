package ro.tuc.ds2020.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("userQueue")
    private String queue;

    @Value("userExchange")
    private String exchange;

    @Value("userRoutingKey")
    private String routing_key;

    @Bean
    public Queue queue(){ return new Queue(queue);}

    @Bean
    public TopicExchange exchange(){ return new TopicExchange(exchange);}

    @Bean
    public Binding binding(){
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routing_key);
    }

    @Bean
    public MessageConverter converter(){return new Jackson2JsonMessageConverter();}

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}

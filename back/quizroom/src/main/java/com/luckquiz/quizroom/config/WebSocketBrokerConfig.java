package com.luckquiz.quizroom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    // Client에서 websocket연결할 때 사용할 API 경로를 설정해주는 메서드.
    // 새로운 핸드쉐이크 커넥션을 생성할 때 사용됨.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/connect/quiz").setAllowedOriginPatterns("*").setHandshakeHandler(new CustomHandshakeHandler());
    }

    // adasd
    // "/queue", "/topic" 이 두 경로가 prefix(api 경로 맨 앞)에 붙은 경우, messageBroker가 잡아서 해당 채팅방을 구독하고 있는 클라이언트에게 메시지를 전달해줌
    // 주로 "/queue"는 1대1 메시징, "/topic"은 1대다 메시징일 때 주로 사용함.
    @Override
    public void configureMessageBroker (MessageBrokerRegistry registry) {  // 메시지 브로커 설정
        //15초 간격 핑퐁
        registry.enableSimpleBroker( "/queue","/topic").setTaskScheduler(new DefaultManagedTaskScheduler()).setHeartbeatValue(new long[] {30000L,30000L}) ;  // topic은 주로 일대다 여기다 받는걸 해두면 된다.
        // 내장 브로커 사용
        // 파라미터로 설정된 값으로 prefix가 붙은 메시지를 발행 시 브로커가 처리하겠다.
        registry.setApplicationDestinationPrefixes("/app");
//        registry.setApplicationDestinationPrefixes("/app","/topic/quiz");  // 로 가면 app 이 붙은 애를 처리하는 애한테 간다.
        // 메시지 핸들러로 라우팅되는 prefix
        // 클라이언트가 메시지를 보낼 때 경로 맨앞에 "/app"이 붙어있으면 Broker로 보내짐.
//        registry.setUserDestinationPrefix("/topic/quiz");
    }


//    @Override
//    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
//        registry.setMessageSizeLimit(1024*3000);
//        WebSocketMessageBrokerConfigurer.super.configureWebSocketTransport(registry);
//    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // 내가 추가함
        //버퍼 사이즈 8MB로 수정. (2023-05-15)
        registration.setMessageSizeLimit(1024*1024*8);
        registration.setSendBufferSizeLimit(1024*1024*8);
        registration.setSendTimeLimit(20 * 1000);
//        registration.setDecoratorFactories(new AgentWebSocketHandlerDecoratorFactory());
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        //버퍼 사이즈 8MB로 수정. (2023-05-15)
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean(); // (3)
        container.setMaxTextMessageBufferSize(1024*1024*8); // (4)
        container.setMaxBinaryMessageBufferSize(1024*1024*8); // (5)
        container.setMaxSessionIdleTimeout(1200000L); // 1200000ms => 1200sec => 20
        return container;
    }

//    @Bean
//    public WebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory() {
//        return new WebSocketHandlerDecoratorFactory() {
//            @Override
//            public WebSocketHandler decorate(WebSocketHandler handler) {
//                return new MyStompSessionHandler(handler);
//            }
//        };
//    }

}
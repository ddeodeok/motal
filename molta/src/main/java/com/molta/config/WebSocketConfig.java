package com.molta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@Configuration
@EnableWebSocketMessageBroker  // WebSocket과 STOMP 메시징을 활성화하는 애너테이션
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
        // 메시지 브로커 설정 (서버에서 메시지를 전송하는 경로 설정)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");  // Simple 메시지 브로커를 활성화하고 "/topic"으로 메시지 발행
        registry.setApplicationDestinationPrefixes("/app");  // 클라이언트에서 메시지를 보낼 경로 "/app"
    }
    // STOMP 엔드포인트 등록 (클라이언트가 이 엔드포인트로 연결)
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")  // 클라이언트에서 연결할 URL
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
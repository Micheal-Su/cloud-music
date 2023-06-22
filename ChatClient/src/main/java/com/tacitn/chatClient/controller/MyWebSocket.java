package com.tacitn.chatClient.controller;

import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/wsdemo")
@Component
public class MyWebSocket {
	private static int onlineCount = 0;
	private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
	private Session session;
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		session.setMaxIdleTimeout(360000);
		webSocketSet.add(this);		
		addOnlineCount();		
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());	
		try {		
//			sendMessage("连接已建立成功.");
		} catch (Exception e) {		
			System.out.println("IO异常");		
		}
	}
 
	@OnClose
	public void onClose() {	
		webSocketSet.remove(this);		
		subOnlineCount();	
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());	
	}
 
	@OnMessage
	public void onMessage(String message, Session session) {	
		System.out.println("来自客户端的消息:" + message);
		webSocketSet.forEach(c->{
			try {
				c.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
 
	@OnError
	public void onError(Session session, Throwable error) {	
		System.out.println("发生错误");	
		error.printStackTrace();	
	}


	public synchronized void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);	
	}
 
	public static synchronized int getOnlineCount() {	
		return onlineCount;	
	}
 
	public static synchronized void addOnlineCount() {	
		MyWebSocket.onlineCount++;	
	}
 
	public static synchronized void subOnlineCount() {	
		MyWebSocket.onlineCount--;	
	}
 
	public Session getSession() {	
		return session;	
	}
 
	public void setSession(Session session) {	
			this.session = session;	
	}
 
	public static CopyOnWriteArraySet<MyWebSocket> getWebSocketSet() {	
		return webSocketSet;	
	}
 
	public static void setWebSocketSet(CopyOnWriteArraySet<MyWebSocket> webSocketSet) {	
			MyWebSocket.webSocketSet = webSocketSet;	
	}
}
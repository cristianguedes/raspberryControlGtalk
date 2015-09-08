package com.raspberry.demos;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.proxy.ProxyInfo.ProxyType;
import org.jivesoftware.smack.util.StringUtils;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PrincipalRecebeEnviaMsgGtalk extends Thread{
    private static XMPPConnection xmppConnection;
    
   
    public void connect(String server, int port, String s) throws Exception {
    	//configurar proxy caso seja necessário 
    	//ProxyInfo proxy = new ProxyInfo(ProxyType.HTTP,"proxy_internet",8080, "usuario","senha") ;
    	xmppConnection = new XMPPConnection(new ConnectionConfiguration(server, port,s/*,proxy*/));
        xmppConnection.connect();
    }

    public void disconnect(){
        if(xmppConnection != null){
            xmppConnection.disconnect();
            interrupt();
        }
    }

    public void login(String username, String password) throws Exception{
       
    	connect("talk.google.com", 5222, "gmail.com");
        xmppConnection.login(username, password);
        
    }

    public void run(){
        try {
        	
        	//boolean online = isInternetReachable();
       	 
        	//if (online){
        		Thread.sleep(45000);
        		//gmail do raspberry
        		login("raspberrycasa@gmail.com", "senha");
        	//}
        	    
            System.out.println("Login successful");
            listeningForMessages();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
       	  PrincipalRecebeEnviaMsgGtalk gtd = new PrincipalRecebeEnviaMsgGtalk();
       	  gtd.run();
    }

    public void listeningForMessages() {
    	
    	 // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();
        
        // provision gpio pin #01 as an output pin and turn on
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "MyLED", PinState.HIGH);
        System.out.println("--> GPIO state should be: ON");
        
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // turn off gpio pin #00
        pin.low();
    	
    	
        PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class));
        PacketCollector collector = xmppConnection.createPacketCollector(filter);
        boolean situacao = false;
        FreeTTS voice = new FreeTTS();
        while (true) {
        	
            Packet packet = collector.nextResult();
            if (packet instanceof Message) {
                Message message = (Message) packet;
                if (message != null && message.getBody() != null){
                    System.out.println("Received message from " + StringUtils.parseName(message.getFrom()) + " : "
                    					+ (message != null ? message.getBody() : "NULL"));
                    if (message != null && message.getBody().trim().equalsIgnoreCase("desliga") || message != null && message.getBody().equalsIgnoreCase("desligar")) {
                    	 // turn off gpio pin #00 
                       pin.low();
                    	enviaResposta("desligado");
                    	voice.fala("foi desligado! Cristian");
                  	  	situacao = false;
                    }
                    
                    if (message != null && message.getBody().trim().equalsIgnoreCase("liga") || (message != null && message.getBody().equalsIgnoreCase("ligar"))) {
                   	 	// turn off gpio pin #00
                         pin.high();
                    	 enviaResposta("ligado");
                    	 voice.fala("foi ligado! Cristian");
                    	 situacao = true;
                    }
                    
                    if (message != null && message.getBody().trim().equalsIgnoreCase("status")) {
                    	 enviaResposta(situacao?"ligado":"desligado");		
                    	 voice.fala("status "+(situacao?"ligado":"desligado")+"? Cristian");
                    }
                    
                    
                    if (message != null && message.getBody().equalsIgnoreCase("menu")) {
                   	 	enviaResposta("liga - desliga - status - sair");	
                   	 	voice.fala("menu liga - menu desliga - menu status");
                    }
                    
                    if (message != null && message.getBody().equalsIgnoreCase("Rejane")) {
                    	voice.fala("TE AMO -LI-NDA");
					}
                    if (message != null && message.getBody().equalsIgnoreCase("pisca") || message != null && message.getBody().equalsIgnoreCase("piscar")) {
                    	enviaResposta("piscando");
                    	pin.pulse(1000, true);
					}
                   
                    
                    if (message != null && message.getBody().equalsIgnoreCase("sair")) {
                    	 gpio.shutdown();
                    	 voice.fala("saiu");
	                   	 xmppConnection.disconnect();
	                   	 System.exit(0);
                   }
                    if (message != null && message.getBody().length()>5 && message.getBody().trim().substring(0, 4).equalsIgnoreCase("fala")) { 
                    	
                    	voice.fala(message.getBody().trim().substring(5));
                    	
                    }
                    
                   
                    
                }    
            }
        }
        
    }

	private void enviaResposta(String msg) {
		EnviaMsgGtalk enviaMsg = new EnviaMsgGtalk();   
		  try {
			enviaMsg.sendMessage(msg, "emailDestino@gmail.com", xmppConnection);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isInternetReachable()
    {
        try {
            // URL do destino escolhido
            URL url = new URL("http://www.google.com");

            // abre a conexão
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

            // tenta buscar conteúdo da URL
            // se não tiver conexão, essa linha irá falhar
            Object objData = urlConnect.getContent();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            isInternetReachable();
        }
        catch (IOException e) {
            e.printStackTrace();
            isInternetReachable();
        }
        return true;
    }

}
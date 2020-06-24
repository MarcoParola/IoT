package server;

import java.net.InetAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;


public class IotResource {

	String name;
	String address;
	String path;
	String coapBuilder;
	
	public IotResource(String name_, String address_, String path_, String coapBuilder_) {
		name = name_;
		address = address_;
		path = path_;
		coapBuilder = coapBuilder_;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getCoapBuilder() {
		return coapBuilder;
	}
	
}

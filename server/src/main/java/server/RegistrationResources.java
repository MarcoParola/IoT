package server;

import java.net.InetAddress;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;


public class RegistrationResources extends CoapResource {

	CoapServerIot server;
	
	public RegistrationResources(String name, CoapServerIot s) {
		super(name);
		setObservable(true);
		server = s;
 	}
	
	public void handleGET(CoapExchange exchange) {
 		
		
		exchange.accept();
		
        InetAddress addr = exchange.getSourceAddress();
        String uri = new String("coap://[" + addr.toString().substring(1) + "]:5683/.well-known/core");
        CoapClient req = new CoapClient(uri);

        String response = req.get().getResponseText().replace("</.well-known/core>;", "");

        System.out.println(response);
        
        // Create a new class resource for each resource
        for (String res : response.split("\n")) {

        	// TODO controlla che il codice inizi con 2
        	ResourceCoapClient new_resource = new ResourceCoapClient(addr.toString().substring(1), res);

        	IotResource iot_res = new IotResource(new_resource.getName(), new_resource.getAddr(), new_resource.getPath(), res);
        	
        	
        	if(!server.resources_dictionary.containsKey(new_resource.getName()))
        		server.resources_dictionary.put(new_resource.getName(), iot_res);
        	
        	
            /*for (int i = 0; i < CoapServerIot.resources_array.size(); i++)
                if (new_resource.getAddr().equals(CoapServerIot.resources_array.get(i).getAddr())
                        && new_resource.getPath().equals(CoapServerIot.resources_array.get(i).getPath()))
                    return;*/

            //CoapServerIot.resources_array.add(new_resource);
           /* System.out.println("\nThe resource " + new_resource.getName() + new_resource.getAddr() + '\n'
            + new_resource.getPath() + '\n' +" has been registered \n\n\n") ;*/
            
            
            //server.add((CoapResource)new IotResource(new_resource.getName()));
		
		
        }
 	}
}

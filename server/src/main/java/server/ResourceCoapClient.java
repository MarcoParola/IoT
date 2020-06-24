package server;

import java.util.Queue;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;

public class ResourceCoapClient extends CoapClient {

	 private String addr;
	    private String name;
	    private String path;
	    private String methods;
	    private boolean isObservable = false;
	    
	    /* DELETE
	    private boolean isInRoom = false;
	    private CoapObserveRelation relation;
	    private CoapHandlerObs handlerObs;
*/
	    
	    
	    // Create a resource
	    public ResourceCoapClient(String addr, String content) {
	        super();

	        String[] content_split = content.split(";");

	        this.addr = addr;
	        this.name = content_split[1].substring(content_split[1].indexOf("=") + 2,
	                content_split[1].lastIndexOf("\""));
	        this.path = content_split[0].substring(content_split[0].indexOf("<") + 1,
	                content_split[0].indexOf(">"));
	        this.methods = content_split[2];
	        this.isObservable = content.contains("obs");

	        this.setURI("coap://[" + this.addr + "]" + this.path);

	        // If the resource is observable, create the CoapHandlerObs to receive the notify from the
	        // server
	        
	        
	        
	        /* DELETE
	        if (this.isObservable) {
	            this.handlerObs = new CoapHandlerObs();
	            this.relation = this.observe(this.handlerObs);
	        }*/
	    }

	    // Return the address of the resource
	    public String getAddr() {
	        return this.addr;
	    }

	    // Return the path of the resource
	    public String getPath() {
	        return this.path;
	    }

	    // Return the name of the resource
	    public String getName() {
	        return this.name;
	    }

	    // Return the format of the payload to perform the POST/PUT method
	    public String getPostPutFormat() {
	        return this.methods.substring(this.methods.indexOf(",") + 2);
	    }

	    // Check if the resource has a method
	    public boolean hasMethod(String method) {
	        return this.methods.contains(method.toUpperCase());
	    }

	    // Rename the resource
	    public void renameResource(String name) {
	        this.name = name;
	    }

	    // Check if the resource is observable
	    public boolean isObservable() {
	        return this.isObservable;
	    }

	    
	    /* DELETE
	    // Set to true the private variable if the resource is assigned to a room
	    public void setInRoom(boolean b) {
	        this.isInRoom = b;
	    }

	    // Return if the resource is in a room
	    public boolean getInRoom() {
	        return this.isInRoom;
	    }

	    // Return the queue of the values history
	    public Queue<String> getQueueObserve() {
	        return this.handlerObs.getQueue();

	    }
	    */
	    
}

package client;

import org.eclipse.californium.core.CoapClient;

public class ResourceCoapClient extends CoapClient {

	 private String addr;
	    private String name;
	    private String path;
	    private String methods;
	    private boolean isObservable = false;
	    
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
	        
	    }


	    public String getAddr() {
	        return this.addr;
	    }


	    public String getPath() {
	        return this.path;
	    }


	    public String getName() {
	        return this.name;
	    }


	    public String getPostPutFormat() {
	        return this.methods.substring(this.methods.indexOf(",") + 2);
	    }


	    public boolean hasMethod(String method) {
	        return this.methods.contains(method.toUpperCase());
	    }


	    public void renameResource(String name) {
	        this.name = name;
	    }


	    public boolean isObservable() {
	        return this.isObservable;
	    }

	    
}
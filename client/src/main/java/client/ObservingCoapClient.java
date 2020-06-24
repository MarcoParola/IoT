package client;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public class ObservingCoapClient extends CoapClient {

	CoapObserveRelation relation = null;
	private String addr;
    private String name;
    private String path;
    private String methods;
    private boolean isObservable = true;
    JFrame frame = null;
    JTextField text = null;
    
	public ObservingCoapClient(String addr, String content) {		
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
	
	public void start() {
		
		frame = new JFrame("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        text = new JTextField();
        frame.getContentPane().add(text);
        frame.setVisible(true);
        
		relation = this.observe(new ObservingHandler(text), MediaTypeRegistry.APPLICATION_JSON);
		System.out.println("Start Observing\n");
	}
	
	public void stop() {
		frame.setVisible(false);
		frame.dispose();
		relation.proactiveCancel();
		
		System.out.println("Stop Observing\n");
	}
	
}

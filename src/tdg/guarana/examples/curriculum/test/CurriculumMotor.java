package tdg.guarana.examples.curriculum.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.w3c.dom.Document;

import file.text.TextWriter;
import guarana.framework.message.Message;
import guarana.toolkit.engine.Scheduler;
import guarana.util.xml.XMLHandler;
import tdg.guarana.examples.curriculum.solution.IntProcess;

public class CurriculumMotor {

	public static String             STATS_FILE                 = "_curriculum-results-msg.txt";
	public static String             STATS_FOLDER;
	
	private ArrayList<Message<Document>> msgList;
	
	public static void main(String[] args) {
		System.out.println("<Simulation started!>");
		System.out.println("---- Curriculum Lattes Msg ----");
		new CurriculumMotor().start();
		System.out.println("<Simulation finished!>");
	}

	public void start(){
		
		String summaryFile = STATS_FOLDER+"execution-summary.txt";
		
		String msg = ">>> Building messages... ";
		System.out.println(msg);

		this.msgList = buildMessages(1);
				
		Scheduler exec = new Scheduler("Scheduler");
		IntProcess prc = new IntProcess();
		exec.registerProcess( prc );
		
		msg = "Starting work... ";
		System.out.println(msg);
		TextWriter.writeString2File(msg, summaryFile, true);
		
		exec.start();				
							
		msg = "System running...";
		System.out.println(msg);
		TextWriter.writeString2File(msg, summaryFile, true);
		
		//prc.communicatorEntry.pushRead(this.msgList);
				
		CurriculumWorker pusher = new CurriculumWorker(1, this.msgList, prc, summaryFile); 
		pusher.start();	
		
	}
	
	private ArrayList<Message<Document>> buildMessages(long messages) {
		ArrayList<Message<Document>> result = new ArrayList<Message<Document>>();
	    
		long start = System.currentTimeMillis();
		
       		Document docX1 = XMLHandler.readXmlFile("./curriculo-rafael.xml");
       		docX1.getElementsByTagName("CURRICULO-VITAE").item(1).setTextContent(UUID.randomUUID().toString());
       		Message<Document> m = new Message<Document>();
       		m.setBody(docX1);
			result.add(m);
		
		long end = System.currentTimeMillis();

		DecimalFormat df = new DecimalFormat("####.##");
        System.out.println(">>> Time to build messages: "+df.format(((end-start)/1000f/60f))+" min");        
		
		return result;
		
	}
	
}
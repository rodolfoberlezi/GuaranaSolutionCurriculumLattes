package tdg.guarana.examples.curriculum.test;

import file.text.TextWriter;
import guarana.framework.message.Message;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;

import tdg.concurrency.activity.ActiveObject;
import tdg.guarana.examples.curriculum.solution.IntProcess;

public class CurriculumWorker extends ActiveObject {

	private int rate;
	private String summaryFile;
	private IntProcess process;
	int count;
	
	private ArrayList<Message<Document>> msgList;
		
	public CurriculumWorker(int rate, ArrayList<Message<Document>> msgList, IntProcess process, String summaryFile) {
		super("Worker");
		this.rate = rate;
		this.msgList = msgList;
		this.process = process;
		this.summaryFile = summaryFile;
	}
		
	@Override
	protected void doWork() {
		
		long start = 0, end = 0;
		int i = 0;
		
		DecimalFormat df = new DecimalFormat("####.##");
		String msg = "Message generation started at: " + new Date();			
		System.out.println(msg);	
		TextWriter.writeString2File(msg, summaryFile, true);	
		
        try {     	
                             
            start = System.currentTimeMillis();
            for (Message<Document> m : msgList) {   		   
       		   	process.communicatorEntry.pushRead(m);
		        Thread.sleep(rate);    
		        i++;
            }  
            end = System.currentTimeMillis();
            
        }catch(Exception ex) { System.out.println(ex); }
        
        msg = "Number of messages produced: "+i;
        System.out.println(msg);
        TextWriter.writeString2File(msg, summaryFile, true);
        
        msg = "Actual production time ---------> "+df.format(((end-start)/1000f/60f))+" min";
        System.out.println(msg);
        TextWriter.writeString2File(msg, summaryFile, true);
        
        msg = "Message production finished!";
        System.out.println(msg);
        TextWriter.writeString2File(msg, summaryFile, true);
        
	} 	

}
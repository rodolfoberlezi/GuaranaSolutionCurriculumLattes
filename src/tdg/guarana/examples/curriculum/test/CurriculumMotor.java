package tdg.guarana.examples.curriculum.test;

import java.nio.file.NotDirectoryException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import file.text.TextWriter;
import guarana.framework.message.Message;
import guarana.toolkit.engine.Scheduler;
import guarana.util.xml.XMLHandler;
import tdg.guarana.examples.curriculum.solution.IntProcess;

public class CurriculumMotor {

    public static String STATS_FILE = "_curriculum-results-msg.txt";
    public static String STATS_FOLDER;

    private Message<Document> msgList;
    // private ArrayList<Message<Document>> msgList;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("<Simulation started!>");
        System.out.println("---- Curriculum Lattes Msg ----");
        new CurriculumMotor().start();
        System.out.println("<Simulation finished!>");
    }

    public void start() throws InterruptedException {

        String summaryFile = STATS_FOLDER + "execution-summary.txt";

        String msg = ">>> Building messages... ";
        System.out.println(msg);

        try {
            this.msgList = buildMessages(1);
        } catch (XPathExpressionException e) {

            e.printStackTrace();
        }

        Scheduler exec = new Scheduler("Scheduler");
        IntProcess prc = new IntProcess();
        exec.registerProcess(prc);

        msg = "Starting work... ";
        System.out.println(msg);
        TextWriter.writeString2File(msg, summaryFile, true);

        exec.start();

        prc.communicatorEntry.pushRead(msgList);

        msg = "System running...";
        System.out.println(msg);
        TextWriter.writeString2File(msg, summaryFile, true);

        Thread.sleep(60 * 10000);

        // CurriculumWorker pusher = new CurriculumWorker(1, this.msgList, prc,
        // summaryFile);
        // pusher.start();
    }

    private Message<Document> buildMessages(long messages) throws XPathExpressionException {
        long start = System.currentTimeMillis();

        Document docX1 = XMLHandler.readXmlFile("curriculo-rafael.xml");
        //Document docX1 = XMLHandler.readXmlFile("fernandohaddad.xml");        

        Message<Document> m = new Message<Document>();
        m.setBody(docX1);

        long end = System.currentTimeMillis();
        DecimalFormat df = new DecimalFormat("####.##");
        System.out.println(">>> Time to build the message: " + df.format(((end - start) / 1000f / 60f)) + " min");

        return m;

    }

    /**
     * private ArrayList<Message<Document>> buildMessages(long messages) throws
     * XPathExpressionException { ArrayList<Message<Document>> result = new
     * ArrayList<Message<Document>>();
     *
     * long start = System.currentTimeMillis();
     *
     * Document docX1 = XMLHandler.readXmlFile("curriculo-rafael.xml");
     * XPathExpression expression =
     * XMLHandler.getXPathExpression("CURRICULO-VITAE/@NUMERO-IDENTIFICADOR");
     * System.out.println((String) expression.evaluate(docX1,
     * XPathConstants.STRING));
     *
     * Message<Document> m = new Message<Document>(); m.setBody(docX1);
     * result.add(m);
     *
     * long end = System.currentTimeMillis();
     *
     * DecimalFormat df = new DecimalFormat("####.##"); System.out.println(">>>
     * Time to build messages: " + df.format(((end - start) / 1000f / 60f)) + "
     * min");
     *
     * return result;
     *
     * }
     */
}

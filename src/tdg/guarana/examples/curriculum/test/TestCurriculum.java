package tdg.guarana.examples.curriculum.test;

import file.text.TextWriter;
import file.xml.XMLParams;
import guarana.framework.message.Message;
import guarana.toolkit.engine.Scheduler;
import guarana.util.xml.XMLHandler;
import tdg.guarana.examples.curriculum.solution.IntProcess;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class TestCurriculum {

    public static int REPETITION_TIMES;
    public static String STATS_FILE = "_curriculum-results-msg.txt";
    public static String EXPERIMENTS_CONF_FILE = "./experiments-conf.xml";
    public static String GUARANA_CONF_TEMPLATE_FILE = "./guarana-conf-template.xml";
    public static String GUARANA_CONF_FILE = "./guarana-conf.xml";

    public static ArrayList<Integer> ARRIVAL_RATES;
    public static ArrayList<Integer> NUMBER_OF_WORKERS;
    public static ArrayList<Integer> NUMBER_OF_MESSAGES;
    public static String STATS_FOLDER;

    private ArrayList<Message<Document>> msgList;

    static {
        String[] params = {"repeat", "arrival-rates", "threads", "number-of-messages", "stats-folder"};
        Map<String, String> values = XMLParams.load(params, EXPERIMENTS_CONF_FILE);

        ARRIVAL_RATES = new ArrayList<Integer>();
        for (String e : values.get("arrival-rates").split(",")) {
            ARRIVAL_RATES.add(new Integer(e.trim()));
        }

        NUMBER_OF_WORKERS = new ArrayList<Integer>();
        for (String e : values.get("threads").split(",")) {
            NUMBER_OF_WORKERS.add(new Integer(e.trim()));
        }

        NUMBER_OF_MESSAGES = new ArrayList<Integer>();
        for (String e : values.get("number-of-messages").split(",")) {
            NUMBER_OF_MESSAGES.add(new Integer(e.trim()));
        }

        REPETITION_TIMES = Integer.parseInt(values.get("repeat").trim());

        STATS_FOLDER = values.get("stats-folder").trim();
    }

    public static void main(String[] args) {
        System.out.println("<Simulation started!>");
        System.out.println("---- Curriculum Lattes Msg ----");
        new TestCurriculum().start();
        System.out.println("<Simulation finished!>");
    }

    public void start() {
        try {
            String msg = null;

            msg = "Threads\tRate\tMsgs\tATime\tSTime\tCTime\tSMemory\tHMemory\tWUProcessed\t(Times in Minutes, Memory in MB)";
            TextWriter.writeString2File(msg, STATS_FOLDER + STATS_FILE, true);

            for (int r : ARRIVAL_RATES) {
                for (int m : NUMBER_OF_MESSAGES) {

                    msg = ">>> Building messages... ";
                    System.out.println(msg);

                    this.msgList = buildMessages(m);

                    msg = ">>> Built: " + this.msgList.size();
                    System.out.println(msg);

                    for (int w : NUMBER_OF_WORKERS) {
                        for (int t = 0; t < REPETITION_TIMES; t++) {

                            String summaryFile = STATS_FOLDER + "execution-summary - [" + w + "] [" + r + "] [" + m + "].txt";

                            long start = System.currentTimeMillis();

                            msg = "Starting system at -------------> " + new Date() + " [" + (t + 1) + "/" + REPETITION_TIMES + "]";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Number of workers -------> " + w;
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Arrival rate ------------> " + r;
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Messages ----------------> " + m;
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            DecimalFormat df = new DecimalFormat("####.##");
                            msg = "Total production time ---> " + df.format(m / (1000f / r) / 60f) + " min";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            Document doc = XMLHandler.readXmlFile(GUARANA_CONF_TEMPLATE_FILE);

                            XPathExpression e1 = XMLHandler.getXPathExpression("//threads");
                            ((NodeList) e1.evaluate(doc, XPathConstants.NODESET)).item(0).setTextContent(Integer.toString(w));

                            XPathExpression e2 = XMLHandler.getXPathExpression("//monitoring/work-queue/file");
                            ((NodeList) e2.evaluate(doc, XPathConstants.NODESET)).item(0).setTextContent(STATS_FOLDER + "work-queue-stats - [" + w + "] [" + r + "] [" + m + "].txt");

                            XPathExpression e3 = XMLHandler.getXPathExpression("//monitoring/workers/file");
                            ((NodeList) e3.evaluate(doc, XPathConstants.NODESET)).item(0).setTextContent(STATS_FOLDER + "workers-stats - [" + w + "] [" + r + "] [" + m + "].txt");

                            XPathExpression e4 = XMLHandler.getXPathExpression("//monitoring/memory/file");
                            ((NodeList) e4.evaluate(doc, XPathConstants.NODESET)).item(0).setTextContent(STATS_FOLDER + "memory-stats - [" + w + "] [" + r + "] [" + m + "].txt");

                            XMLHandler.writeXmlFile(doc, GUARANA_CONF_FILE);

                            Scheduler exec = new Scheduler("Scheduler");
                            IntProcess prc = new IntProcess();
                            exec.registerProcess(prc);

                            msg = "Starting workers... ";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            exec.start();

                            msg = "System running...";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            CurriculumWorker pusher = new CurriculumWorker(r, this.msgList, prc, summaryFile);
                            pusher.start();

                            // Stop the system
                            ArrayList<Long> q = new ArrayList<Long>();
                            boolean stop = false;
                            while (!stop) {
                                q.add(exec.getNumberOfWorkUnitsProcessed());
                                Thread.sleep(5000);
                                if (q.size() == 2) {
                                    stop = true; // Assume the systems is stopped
                                    long x1 = q.get(0);
                                    for (long x2 : q) {
                                        if (x1 != x2) {
                                            stop = false; // Don't stop!
                                        }
                                    }
                                    q.clear();
                                }
                            }

                            msg = "Outputs: " + prc.curriculunsRead;
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Shutting down the solution...";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Stopping workers...";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            String stats = w + "\t" + r + "\t" + m + "\t" + exec.getActualTimeStats() + "\t" + exec.getSystemTimeStats() + "\t" + exec.getCPUTimeStats() + "\t" + exec.getNonHeapMemoryStats() + "\t" + exec.getHeapMemoryStats() + "\t" + exec.getNumberOfWorkUnitsProcessed();;
                            TextWriter.writeString2File(stats, STATS_FOLDER + STATS_FILE, true);

                            exec.stop();
                            long end = System.currentTimeMillis();

                            msg = "Number of work units processed: " + exec.getNumberOfWorkUnitsProcessed();
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "System stopped!";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            msg = "Cleaning up heap...";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            doc = null;
                            e1 = null;
                            e2 = null;
                            e3 = null;
                            e4 = null;
                            exec = null;
                            prc = null;
                            pusher = null;
                            q = null;

                            cleanHeap();

                            msg = "Run for: " + ((end - start) / 1000f / 60f) + " min (Actual time)";
                            System.out.println(msg);
                            TextWriter.writeString2File(msg, summaryFile, true);

                            System.out.println("-----------------------------------------------------------------");

                        }
                    }
                }
            }
        } catch (Exception ex) {
            TextWriter.writeString2File(ex.getMessage(), STATS_FOLDER + "_ERROR.txt", true);
        }
    }

    private ArrayList<Message<Document>> buildMessages(long messages) {
        ArrayList<Message<Document>> result = new ArrayList<Message<Document>>();

        long start = System.currentTimeMillis();
        for (int i = 0; i < messages; i++) {
            Document docX1 = XMLHandler.readXmlFile("./curriculum.xml");
            docX1.getElementsByTagName("Curriculum").item(0).setTextContent(UUID.randomUUID().toString());
            Message<Document> m = new Message<Document>();
            m.setBody(docX1);
            result.add(m);
        }
        long end = System.currentTimeMillis();

        DecimalFormat df = new DecimalFormat("####.##");
        System.out.println(">>> Time to build messages: " + df.format(((end - start) / 1000f / 60f)) + " min");

        return result;

    }

    private void cleanHeap() throws InterruptedException {
        System.runFinalization();
        System.gc();
        try {
            Thread.sleep(1 * 60 * 1000);
        } catch (InterruptedException ex) {
            throw ex;
        }
    }

}

package tdg.guarana.examples.curriculum.solution;

import guarana.framework.message.Exchange;
import guarana.framework.message.Message;
import guarana.framework.port.EntryPort;
import guarana.framework.port.ExitPort;
import guarana.framework.port.OneWayPort;
import guarana.framework.port.SolicitorPort;
import guarana.framework.port.TwoWayPort;
import guarana.framework.process.Process;
import guarana.framework.task.Slot;
import guarana.framework.task.Task;
import guarana.framework.task.TaskExecutionException;
import guarana.toolkit.task.communicators.dummy.InDummyCommunicator;
import guarana.toolkit.task.communicators.dummy.OutDummyCommunicator;
import guarana.toolkit.task.communicators.dummy.OutInDummyCommunicator;
import guarana.toolkit.task.modifiers.ContextBasedContentEnricher;
import guarana.toolkit.task.modifiers.Slimmer;
import guarana.toolkit.task.routers.Correlator;
import guarana.toolkit.task.routers.Merger;
import guarana.toolkit.task.routers.Replicator;
import guarana.toolkit.task.routers.Filter;
import guarana.toolkit.task.transformers.Assembler;
import guarana.toolkit.task.transformers.Translator;

import java.io.IOException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.net.httpserver.HttpExchange;

import file.text.TextWriter;
import file.xml.XMLHandler;

import com.sun.net.httpserver.Filter.Chain;

public class IntProcessOLD extends Process {

    private Slot[] slot;
    private Task[] task;
    private OneWayPort entryPortCurriculum, exitPortCurriculum;
    private TwoWayPort solicitorPortQ1, solicitorPortQ2, solicitorPortH;
    public InDummyCommunicator communicatorEntry;
    public int curriculunsRead;
    public XMLHandler xmlwriter;

    public IntProcessOLD() {
        super("Integration Process");

        slot = new Slot[25];
        for (int i = 0; i < slot.length; i++) {
            slot[i] = new Slot("Slot " + i);
        }

        task = new Task[21];

        // Entry Port
        entryPortCurriculum = new EntryPort("Entry Port Curriculum") {
            public void initialise() {
                setInterSlot(new Slot("Interslot"));

                communicatorEntry = new InDummyCommunicator("Communicator@PortCurriculum");
                communicatorEntry.output[0].bind(getInterSlot());
                setCommunicator(communicatorEntry);
            }
        };
        addPort(entryPortCurriculum);

        // Solicitor Port 1
        solicitorPortQ1 = new SolicitorPort("Solicitor Port") {
            public void initialise() {
                setInterSlotIn(new Slot("InterSlot In"));
                setInterSlotOut(new Slot("InterSlot Out"));

                Task communicator = new OutInDummyCommunicator("OutIn") {
                    public void doWork(Exchange exchange) throws TaskExecutionException {
                        Message<Document> request = (Message<Document>) exchange.input[0].poll();

                        Document docX4 = (Document) request.getBody();
                        Document docQ1 = XMLHandler.newDocument();

                        Element root = docQ1.createElement("CVLattes");
                        docQ1.appendChild(root);

                        Element sequencia = docQ1.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX4.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        root.appendChild(sequencia);

                        Element qualis = docQ1.createElement("QUALIS");
                        qualis.setTextContent("A1");
                        root.appendChild(qualis);

                        Message<Document> outMsg = new Message<Document>(request);
                        outMsg.setBody(docQ1);

                        exchange.output[0].add(outMsg);
                    }
                };

                communicator.input[0].bind(getInterSlotIn()); // Interslot
                // conecta as
                // portas por
                // com um
                // comunicator
                communicator.output[0].bind(getInterSlotOut());
                setCommunicator(communicator);
            }
        };
        addPort(solicitorPortQ1);

        // Solicitor Port 2
        solicitorPortQ2 = new SolicitorPort("Solicitor Port") {
            public void initialise() {
                setInterSlotIn(new Slot("InterSlot In"));
                setInterSlotOut(new Slot("InterSlot Out"));

                Task communicator = new OutInDummyCommunicator("OutIn") {
                    public void doWork(Exchange exchange) throws TaskExecutionException {
                        Message<Document> request = (Message<Document>) exchange.input[0].poll();

                        Document docX7 = (Document) request.getBody();
                        Document docQ2 = XMLHandler.newDocument();

                        Element root = docQ2.createElement("CVLattes");
                        docQ2.appendChild(root);

                        Element sequencia = docQ2.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX7.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        root.appendChild(sequencia);

                        Element qualis = docQ2.createElement("QUALIS");
                        qualis.setTextContent("B1");
                        root.appendChild(qualis);

                        Message<Document> outMsg = new Message<Document>(request);
                        outMsg.setBody(docQ2);

                        exchange.output[0].add(outMsg);

                    }
                };

                communicator.input[0].bind(getInterSlotIn());
                communicator.output[0].bind(getInterSlotOut());
                setCommunicator(communicator);
            }
        };
        addPort(solicitorPortQ2);

        // Solicitor Port 3
        solicitorPortH = new SolicitorPort("Solicitor Port") {
            public void initialise() {
                setInterSlotIn(new Slot("InterSlot In"));
                setInterSlotOut(new Slot("InterSlot Out"));

                Task communicator = new OutInDummyCommunicator("OutIn") {
                    public void doWork(Exchange exchange) throws TaskExecutionException {
                        Message<Document> request = (Message<Document>) exchange.input[0].poll();

                        Document docX8 = (Document) request.getBody();
                        Document docH = XMLHandler.newDocument();

                        Element root = docH.createElement("CVLattes");
                        docH.appendChild(root);

                        Element sequencia = docH.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX8.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        root.appendChild(sequencia);

                        Element hindex = docH.createElement("H-INDEX");
                        hindex.setTextContent("12345");
                        root.appendChild(hindex);

                        Message<Document> outMsg = new Message<Document>(request);
                        outMsg.setBody(docH);

                        exchange.output[0].add(outMsg);
                    }
                };

                communicator.input[0].bind(getInterSlotIn());
                communicator.output[0].bind(getInterSlotOut());
                setCommunicator(communicator);
            }
        };
        addPort(solicitorPortH);

        // Exit Port
        exitPortCurriculum = new ExitPort("Exit Port Curriculum") {
            public void initialise() {
                setInterSlot(new Slot("Interslot"));

                Task communicator = new OutDummyCommunicator("Communicator@PortCurriculum") {
                    public void execute() throws TaskExecutionException {
                        //super.execute();                        
                        Message<Document> msg = (Message<Document>) input[0].getMessage();
                        XMLHandler.writeXmlFile(msg.getBody(), "saida.xml");
                        System.out.println("Curriculum Lattes completely read");
                        //xmlwriter.writeXmlFile(docX2, "outputSolution.xml");
                    }
                };

                communicator.input[0].bind(getInterSlot());
                setCommunicator(communicator);
            }
        };
        addPort(exitPortCurriculum);

        // Tasks
        // Filter
        task[0] = new Filter("Filter t0") {

            public void doWork(Exchange exchange) throws TaskExecutionException {

                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                Document docX1 = (Document) inMsg.getBody();
                XPathExpression expression = XMLHandler.getXPathExpression("CURRICULO-VITAE/@NUMERO-IDENTIFICADOR");
                try {
//                    System.out.println(
//                            "Numero Identificador = " + (String) expression.evaluate(docX1, XPathConstants.STRING));
                    if (expression.evaluate(docX1, XPathConstants.STRING) != null) {
                        // Verifica se o documento enviado é um Lattes
                        exchange.output[0].add(inMsg);
                        System.out.println("aquiT0");
                    } else {
                        System.out.println("The inserted XML Curriculum is not valid");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Curriculum not valid");
                }

            }

        };
        task[0].input[0].bind(entryPortCurriculum.getInterSlot());
        task[0].output[0].bind(slot[0]);
        addTask(task[0]);

        // Slimmer
        task[1] = new Slimmer("Slimmer t1") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                Document docX1 = inMsg.getBody();
                Document docX2 = XMLHandler.newDocument();
                // Reduz o curriculo lattes docX1 completo em uma nova versão
                // docX2
                Element root = docX2.createElement("CVLattes");
                docX2.appendChild(root);

                Element id = docX2.createElement("ID");
                XPathExpression expression = XMLHandler.getXPathExpression("CURRICULO-VITAE/@NUMERO-IDENTIFICADOR");
                try {
                    id.setTextContent((String) expression.evaluate(docX1, XPathConstants.STRING));
                    root.appendChild(id);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("String error");
                }

                Element nome = docX2.createElement("NOME");
                XPathExpression expression2 = XMLHandler
                        .getXPathExpression("CURRICULO-VITAE/DADOS-GERAIS/@NOME-COMPLETO");
                try {
                    nome.setTextContent((String) expression2.evaluate(docX1, XPathConstants.STRING));
                    System.out
                            .println("Nome Completo = " + (String) expression2.evaluate(docX1, XPathConstants.STRING));
                    root.appendChild(nome);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("String error");
                }

//                Element evento = docX2.createElement("EVENTOS");
//                docX2.appendChild(evento);
//                NodeList events = docX1.getElementsByTagName("TRABALHO-EM-EVENTOS");
//
//                for (int i = 0; i < events.getLength(); i++) {
//                    Element d = (Element) events.item(i);
//
//                Element t_eventos = docX2.createElement("TITULO");
//                    XPathExpression expressionE = XMLHandler
//                            .getXPathExpression("TRABALHOS-EM-EVENTOS/TRABALHO-EM-EVENTOS/DADOS-BASICOS-DO-TRABALHO/@TITULO-DO-TRABALHO");
//                    try {
//                        t_eventos.setTextContent((String) expressionE.evaluate(docX1, XPathConstants.STRING));
//                evento.appendChild(t_eventos);
//                        System.out.println("Título do Evento: " + t_eventos);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.out.println("String error");
//                    }
//
//                Element s_eventos = docX2.createElement("SEQUENCIA");
//                    XPathExpression expressionS = XMLHandler
//                            .getXPathExpression("TRABALHOS-EM-EVENTOS/TRABALHO-EM-EVENTOS/@SEQUENCIA-PRODUCAO");
//                    try {
//                        s_eventos.setTextContent((String) expressionS.evaluate(docX1, XPathConstants.STRING));
//                evento.appendChild(s_eventos);
//                        System.out.println("Sequência Evento: " + s_eventos);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        System.out.println("String error");
//                    }
//
//                Element q_eventos = docX2.createElement("QUALIS");
//                evento.appendChild(q_eventos);
                Element eventos = docX2.createElement("EVENTOS");
                eventos.setTextContent(docX1.getElementsByTagName("TRABALHOS-EM-EVENTOS").item(0).getTextContent());
                root.appendChild(eventos);

//                }
                Element artigos = docX2.createElement("ARTIGOS");

                artigos.setTextContent(docX1.getElementsByTagName("ARTIGOS-PUBLICADOS").item(0).getTextContent());
                root.appendChild(artigos);

                Element livros = docX2.createElement("LIVROS");

                livros.setTextContent(docX1.getElementsByTagName("LIVROS-E-CAPITULOS").item(0).getTextContent());
                root.appendChild(livros);

//                Element revistas = docX2.createElement("REVISTAS");
//                revistas.setTextContent(docX1.getElementsByTagName("TEXTOS-EM-JORNAIS-OU-REVISTAS").item(0).getTextContent());
//                root.appendChild(revistas);

                Message<Document> outMsg = new Message<Document>(inMsg);

                outMsg.setBody(docX2);
                exchange.output[0].add(outMsg);

            }
        };
        task[1].input[0].bind(slot[0]);
        task[1].output[0].bind(slot[1]);
        addTask(task[1]);

        // Replicator
        task[2] = new Replicator("Replicator t2", 3);
        task[2].input[0].bind(slot[1]);
        task[2].output[0].bind(slot[2]);
        task[2].output[1].bind(slot[9]);
        task[2].output[2].bind(slot[11]);
        addTask(task[2]);

        // Translator //Conferencias
        task[3] = new Translator("Translator t3") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Transforma um docX2 em um docX3
                Document docX2 = inMsg.getBody();
                Document docX3 = XMLHandler.newDocument();
                System.out.println("aquiT3");
                Element root = docX3.createElement("CVLattes");
                docX3.appendChild(root);

                Element eventos = docX3.createElement("EVENTO");
                eventos.setTextContent(docX2.getElementsByTagName("TRABALHO-EM-EVENTOS").item(0).getTextContent());
                root.appendChild(eventos);

                Element sequencia = docX3.createElement("SEQUENCIA");
                XPathExpression expressions = XMLHandler.getXPathExpression("TRABALHO-EM-EVENTOS/@SEQUENCIA-PRODUCAO");
                try {
                    sequencia.setTextContent((String) expressions.evaluate(docX2, XPathConstants.STRING));
                    System.out.println(
                            "Sequencia Evento = " + (String) expressions.evaluate(docX2, XPathConstants.STRING));
                    eventos.appendChild(sequencia);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Element titulo = docX3.createElement("TITULO");
                XPathExpression expressiont = XMLHandler
                        .getXPathExpression("TRABALHO-EM-EVENTOS/DADOS-BASICOS-DO-TRABALHO/@TITULO-DO-TRABALHO");
                try {
                    titulo.setTextContent((String) expressiont.evaluate(docX2, XPathConstants.STRING));
                    eventos.appendChild(titulo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Element qualis = docX3.createElement("QUALIS");
                eventos.appendChild(qualis);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX3);
                exchange.output[0].add(outMsg);

            }
        };
        task[3].input[0].bind(slot[2]);
        task[3].output[0].bind(slot[3]);
        addTask(task[3]);

        // Replicator
        task[4] = new Replicator("Replicator t4", 2);
        task[4].input[0].bind(slot[3]);
        task[4].output[0].bind(slot[4]);
        task[4].output[1].bind(slot[5]);
        addTask(task[4]);

        // Translator Qualis Q1 de Conferencia
        task[5] = new Translator("Translator t5") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX4 com a Sequ�ncia pedindo por um valor(fixo)
                // para o Qualis de Eventos
                Document docX3 = inMsg.getBody();
                Document docX4 = XMLHandler.newDocument();
                System.out.println("aquiT5");
                Element root = docX4.createElement("CVLattes");
                docX4.appendChild(root);

                Element sequencia = docX4.createElement("SEQUENCIA");
                sequencia.setTextContent(docX3.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Element qualis = docX4.createElement("QUALIS");
                root.appendChild(qualis);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX4);
                exchange.output[0].add(outMsg);

            }
        };
        task[5].input[0].bind(slot[4]);
        task[5].output[0].bind(solicitorPortQ1.getInterSlotIn());
        addTask(task[5]);

        // Correlator
        task[6] = new Correlator("Correlator t6", 2, 2) {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX3 = inMsg0.getBody();
                String sequenciaX3 = docX3.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                Document docQ1 = inMsg1.getBody();
                String sequenciaQ1 = docQ1.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                if (sequenciaX3.equals(sequenciaQ1)) {
                    exchange.output[0].add(inMsg0);
                    exchange.output[1].add(inMsg1);
                }
            }

        };
        task[6].input[0].bind(slot[5]);
        task[6].input[1].bind(solicitorPortQ1.getInterSlotOut());
        task[6].output[0].bind(slot[6]);
        task[6].output[1].bind(slot[7]);
        addTask(task[6]);

        // Context Based Content Enricher
        task[7] = new ContextBasedContentEnricher("ContextBasedContentEnricher t7") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX3 = inMsg0.getBody();
                Document docQ1 = inMsg1.getBody();

                docX3.getElementsByTagName("QUALIS").item(0)
                        .setTextContent(docQ1.getElementsByTagName("QUALIS").item(0).getTextContent());
                exchange.output[0].add(inMsg0);
            }

        };
        task[7].input[0].bind(slot[6]);
        task[7].input[1].bind(slot[7]);
        task[7].output[0].bind(slot[8]);
        addTask(task[7]);

        // Translator //Livros
        task[8] = new Translator("Translator t8") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Transforma um docX2 em um docX5
                Document docX2 = inMsg.getBody();
                Document docX5 = XMLHandler.newDocument();
                System.out.println("aquiT8");
                Element root = docX5.createElement("CVLattes");
                docX5.appendChild(root);

                Element livros = docX5.createElement("LIVRO");
                livros.setTextContent(docX2.getElementsByTagName("LIVRO-PUBLICADO-OU-ORGANIZADO").item(0).getTextContent());
                root.appendChild(livros);

                Element sequencia = docX5.createElement("SEQUENCIA");
//                XPathExpression expressions = XMLHandler.getXPathExpression(
//                        "LIVROS-PUBLICADOS-OU-ORGANIZADOS/LIVRO-PUBLICADO-OU-ORGANIZADO/@SEQUENCIA-PRODUCAO");
//                try {
//                    sequencia.setTextContent((String) expressions.evaluate(docX2, XPathConstants.STRING));
//                    System.out.println(
//                            "Sequencia Livro = " + (String) expressions.evaluate(docX2, XPathConstants.STRING));
                    livros.appendChild(sequencia);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Element titulo = docX5.createElement("TITULO");
//                XPathExpression expressiont = XMLHandler.getXPathExpression(
//                        "LIVROS-PUBLICADOS-OU-ORGANIZADOS/LIVRO-PUBLICADO-OU-ORGANIZADO/DADOS-BASICOS-DO-LIVRO/@TITULO-DO-LIVRO");
//                try {
//                    titulo.setTextContent((String) expressiont.evaluate(docX2, XPathConstants.STRING));
                    livros.appendChild(titulo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX5);
                exchange.output[0].add(outMsg);

            }
        };
        task[8].input[0].bind(slot[9]);
        task[8].output[0].bind(slot[10]);
        addTask(task[8]);

        // Translator //Periodicos
        task[9] = new Translator("Translator t9") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Transforma um docX2 em um docX6
                Document docX2 = inMsg.getBody();
                Document docX6 = XMLHandler.newDocument();
                System.out.println("aquiT9");
                Element root = docX6.createElement("CVLattes");
                docX6.appendChild(root);

                Element artigos = docX6.createElement("ARTIGO");
                artigos.setTextContent(docX2.getElementsByTagName("ARTIGO-PUBLICADO").item(0).getTextContent());
                root.appendChild(artigos);

                Element sequencia = docX6.createElement("SEQUENCIA");
//                XPathExpression expressions = XMLHandler
//                        .getXPathExpression("ARTIGOS-PUBLICADOS/ARTIGO-PUBLICADO/@SEQUENCIA-PRODUCAO");
//                try {
//                    sequencia.setTextContent((String) expressions.evaluate(docX2, XPathConstants.STRING));
//                    System.out.println(
//                            "Sequencia Periodico = " + (String) expressions.evaluate(docX2, XPathConstants.STRING));
                    artigos.appendChild(sequencia);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Element titulo = docX6.createElement("TITULO");
//                XPathExpression expressiont = XMLHandler
//                        .getXPathExpression("ARTIGOS-PUBLICADOS/ARTIGO-PUBLICADO/DADOS-BASICOS-DO-ARTIGO/@TITULO-DO-ARTIGO");
//                try {
//                    titulo.setTextContent((String) expressiont.evaluate(docX2, XPathConstants.STRING));
                    artigos.appendChild(titulo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Element qualis = docX6.createElement("QUALIS");
                artigos.appendChild(qualis);

                Element hindex = docX6.createElement("H-INDEX");
                artigos.appendChild(hindex);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX6);
                exchange.output[0].add(outMsg);
            }
        };
        task[9].input[0].bind(slot[11]);
        task[9].output[0].bind(slot[12]);
        addTask(task[9]);

        // Replicator
        task[10] = new Replicator("Replicator t10", 2);
        task[10].input[0].bind(slot[13]);
        task[10].output[0].bind(slot[14]);
        task[10].output[1].bind(slot[15]);
        addTask(task[10]);

        // Translator Qualis Q2 //Periodicos
        task[11] = new Translator("Translator t11") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX7 com a Sequ�ncia pedindo por um valor(fixo)
                // para o Qualis de Revistas
                Document docX6 = inMsg.getBody();
                Document docX7 = XMLHandler.newDocument();
                System.out.println("aquiT11");
                Element root = docX7.createElement("CVLattes");
                docX7.appendChild(root);

                Element sequencia = docX7.createElement("SEQUENCIA");
//                sequencia.setTextContent(docX6.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Element qualis = docX7.createElement("QUALIS");
                root.appendChild(qualis);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX7);
                exchange.output[0].add(outMsg);
            }
        };
        task[11].input[0].bind(slot[14]);
        task[11].output[0].bind(solicitorPortQ2.getInterSlotIn());
        addTask(task[11]);

        // Correlator
        task[12] = new Correlator("Correlator t12", 2, 2) {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX6 = inMsg0.getBody();
                String sequenciaX6 = docX6.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                Document docQ2 = inMsg1.getBody();
                String sequenciaQ2 = docQ2.getElementsByTagName("SEQUENCIA").item(0).getTextContent();
                if (sequenciaX6.equals(sequenciaQ2)) {
                    exchange.output[0].add(inMsg0);
                    exchange.output[1].add(inMsg1);
                }
            }

        };
        task[12].input[0].bind(slot[14]);
        task[12].input[1].bind(solicitorPortQ2.getInterSlotOut());
        task[12].output[0].bind(slot[15]);
        task[12].output[1].bind(slot[16]);
        addTask(task[12]);

        // Context Based Content Enricher
        task[13] = new ContextBasedContentEnricher("ContextContentEnricher t13") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX6 = inMsg0.getBody();
                Document docQ2 = inMsg1.getBody();

                docX6.getElementsByTagName("QUALIS").item(0)
                        .setTextContent(docQ2.getElementsByTagName("QUALIS").item(0).getTextContent());
                exchange.output[0].add(inMsg0);
            }

        };
        task[13].input[0].bind(slot[15]);
        task[13].input[1].bind(slot[16]);
        task[13].output[0].bind(slot[17]);
        addTask(task[13]);

        // Replicator
        task[14] = new Replicator("Replicator t14", 2);
        task[14].input[0].bind(slot[17]);
        task[14].output[0].bind(slot[18]);
        task[14].output[1].bind(slot[19]);
        addTask(task[14]);

        // Translator //H-index periodicos
        task[15] = new Translator("Translator t15") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX8 com a Sequ�ncia pedindo por um valor(fixo)
                // para o H-Index de Revistas
                Document docX6 = inMsg.getBody();
                Document docX8 = XMLHandler.newDocument();
                System.out.println("aquiT15");
                Element root = docX8.createElement("CVLattes");
                docX8.appendChild(root);

                Element sequencia = docX8.createElement("SEQUENCIA");
                sequencia.setTextContent(docX6.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Element hindex = docX8.createElement("H-INDEX");
                root.appendChild(hindex);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX8);
                exchange.output[0].add(outMsg);
            }
        };
        task[15].input[0].bind(slot[18]);
        task[15].output[0].bind(solicitorPortH.getInterSlotIn());
        addTask(task[15]);

        // Correlator
        task[16] = new Correlator("Correlator t16", 2, 2) {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX6 = inMsg0.getBody();
                String sequenciaX6 = docX6.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                Document docH = inMsg1.getBody();
                String sequenciaH = docH.getElementsByTagName("SEQUENCIA").item(0).getTextContent();
                if (sequenciaX6.equals(sequenciaH)) {
                    exchange.output[0].add(inMsg0);
                    exchange.output[1].add(inMsg1);
                }
            }

        };
        task[16].input[0].bind(slot[19]);
        task[16].input[1].bind(solicitorPortH.getInterSlotOut());
        task[16].output[0].bind(slot[20]);
        task[16].output[1].bind(slot[21]);
        addTask(task[16]);

        // Context Based Content Enricher
        task[17] = new ContextBasedContentEnricher("ContextContentEnricher t17") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX6 = inMsg0.getBody();
                Document docH = inMsg1.getBody();

                docX6.getElementsByTagName("QUALIS").item(0)
                        .setTextContent(docH.getElementsByTagName("QUALIS").item(0).getTextContent());
                exchange.output[0].add(inMsg0);
            }

        };
        task[17].input[0].bind(slot[20]);
        task[17].input[1].bind(slot[21]);
        task[17].output[0].bind(slot[22]);
        addTask(task[17]);

        // Merger
        task[18] = new Merger("Merger t18", 3);
        task[18].input[0].bind(slot[8]);
        task[18].input[1].bind(slot[10]);
        task[18].input[2].bind(slot[22]);
        task[18].output[0].bind(slot[23]);
        addTask(task[18]);

        // Assembler
        task[19] = new Assembler("Assembler t19", 1) {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();

                Document docX9 = inMsg0.getBody();
                System.out.println("aquiT19");
                exchange.output[0].add(inMsg0);
            }

        };
        task[19].input[0].bind(slot[23]);
        task[19].output[0].bind(slot[24]);
        addTask(task[19]);

        // Translator //xml final
        task[20] = new Translator("Translator t20") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();

                Document docX9 = inMsg0.getBody();
                System.out.println("aquiT20");
                Message<Document> outMsg = new Message<Document>();
                outMsg.setBody(docX9);

                exchange.output[0].add(outMsg);
            }
        };
        task[20].input[0].bind(slot[24]);
        task[20].output[0].bind(exitPortCurriculum.getInterSlot());
        addTask(task[20]);

    }

}

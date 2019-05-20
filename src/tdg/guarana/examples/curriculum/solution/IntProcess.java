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
import guarana.toolkit.task.transformers.Translator;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import file.xml.XMLHandler;

import guarana.toolkit.task.routers.Dispatcher;
import guarana.toolkit.task.transformers.Aggregator;
import guarana.toolkit.task.transformers.Splitter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.NodeList;

public class IntProcess extends Process {

    private Slot[] slot;
    private Task[] task;
    private OneWayPort entryPortCurriculum, exitPortCurriculum;
    private TwoWayPort solicitorPortQ1, solicitorPortQ2, solicitorPortH;
    public InDummyCommunicator communicatorEntry;
    public int curriculunsRead;
    public XMLHandler xmlwriter;

    public IntProcess() {
        super("Integration Process");

        slot = new Slot[26];
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

                        Document docQ1 = (Document) request.getBody();

                        Element qualis = docQ1.createElement("QUALIS");
                        qualis.setTextContent("A1");
                        docQ1.getFirstChild().appendChild(qualis);

                        Message<Document> outMsg = new Message<Document>(request);
                        outMsg.setBody(docQ1);

                        exchange.output[0].add(outMsg);
                    }
                };

                communicator.input[0].bind(getInterSlotIn()); // Interslot               
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

                        Document docQ2 = (Document) request.getBody();

                        Element qualis = docQ2.createElement("QUALIS");
                        qualis.setTextContent("B1");
                        docQ2.getFirstChild().appendChild(qualis);

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

                        Document docH = (Document) request.getBody();

                        Element hindex = docH.createElement("H-INDEX");
                        hindex.setTextContent("12345");
                        docH.getFirstChild().appendChild(hindex);

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
                        Message<Document> msg = (Message<Document>) input[0].getMessage();
                        XMLHandler.writeXmlFile(msg.getBody(), "saida.xml");
                        System.out.println("Curriculum Lattes completely read");
                    }
                };

                communicator.input[0].bind(getInterSlot());
                setCommunicator(communicator);
            }
        };
        addPort(exitPortCurriculum);

        // Tasks
        // Slimmer
        task[0] = new Slimmer("Slimmer t0") {

            public void doWork(Exchange exchange) throws TaskExecutionException {

                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                Document docX1 = (Document) inMsg.getBody();
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
                XPathExpression expression2 = XMLHandler.getXPathExpression("CURRICULO-VITAE/DADOS-GERAIS/@NOME-COMPLETO");
                try {
                    nome.setTextContent((String) expression2.evaluate(docX1, XPathConstants.STRING));
                    System.out.println("Nome Completo = " + (String) expression2.evaluate(docX1, XPathConstants.STRING));
                    root.appendChild(nome);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("String error");
                }

                Element producao = docX2.createElement("PRODUCAO-BIBLIOGRAFICA");
                try {
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Source xslt = new StreamSource("slimmer.xslt");
                    Transformer transformer = factory.newTransformer(xslt);
                    Source text = new DOMSource(docX1);
                    transformer.transform(text, new DOMResult(producao));
                } catch (Exception ex) {
                    Logger.getLogger(IntProcess.class.getName()).log(Level.SEVERE, null, ex);
                }
                root.appendChild(producao);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX2);
                exchange.output[0].add(outMsg);

            }

        };
        task[0].input[0].bind(entryPortCurriculum.getInterSlot());
        task[0].output[0].bind(slot[0]);
        addTask(task[0]);

        // Splitter
        task[1] = new Splitter("Splitter t1") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                Document docX2 = inMsg.getBody();

                NodeList eventos = docX2.getElementsByTagName("TRABALHO-EM-EVENTOS");

                for (int i = 0; i < eventos.getLength(); i++) {
                    Element ev = (Element) eventos.item(i);

                    Document docX3 = XMLHandler.newDocument();

                    Element rootX3 = docX3.createElement("TRABALHO-EM-EVENTOS");
                    docX3.appendChild(rootX3);

                    Element titulo = docX3.createElement("TITULO");
                    titulo.setTextContent(ev.getElementsByTagName("TITULO").item(0).getTextContent());
                    rootX3.appendChild(titulo);

                    Element sequencia = docX3.createElement("SEQUENCIA");
                    sequencia.setTextContent(ev.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                    rootX3.appendChild(sequencia);

                    Element autores = docX3.createElement("AUTORES");
                    autores.setTextContent(ev.getElementsByTagName("AUTORES").item(0).getTextContent());
                    rootX3.appendChild(autores);

                    Element evento = docX3.createElement("EVENTO");
                    evento.setTextContent(ev.getElementsByTagName("EVENTO").item(0).getTextContent());
                    rootX3.appendChild(evento);

                    Element local = docX3.createElement("LOCAL");
                    local.setTextContent(ev.getElementsByTagName("LOCAL").item(0).getTextContent());
                    rootX3.appendChild(local);

                    Element anais = docX3.createElement("ANAIS");
                    anais.setTextContent(ev.getElementsByTagName("ANAIS").item(0).getTextContent());
                    rootX3.appendChild(anais);

                    Element paginas = docX3.createElement("PAGINAS");
                    paginas.setTextContent(ev.getElementsByTagName("PAGINAS").item(0).getTextContent());
                    rootX3.appendChild(paginas);

                    Element ano = docX3.createElement("ANO");
                    ano.setTextContent(ev.getElementsByTagName("ANO").item(0).getTextContent());
                    rootX3.appendChild(ano);

                    Message<Document> outMsg = new Message<Document>(inMsg);
                    outMsg.setBody(docX3);
                    exchange.output[0].add(outMsg);
                }

                NodeList artigos = docX2.getElementsByTagName("ARTIGO-PUBLICADO");

                for (int i = 0; i < artigos.getLength(); i++) {
                    Element ar = (Element) artigos.item(i);

                    Document docX4 = XMLHandler.newDocument();

                    Element rootX4 = docX4.createElement("ARTIGO-PUBLICADO");
                    docX4.appendChild(rootX4);

                    Element titulo = docX4.createElement("TITULO");
                    titulo.setTextContent(ar.getElementsByTagName("TITULO").item(0).getTextContent());
                    rootX4.appendChild(titulo);

                    Element sequencia = docX4.createElement("SEQUENCIA");
                    sequencia.setTextContent(ar.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                    rootX4.appendChild(sequencia);

                    Element autores = docX4.createElement("AUTORES");
                    autores.setTextContent(ar.getElementsByTagName("AUTORES").item(0).getTextContent());
                    rootX4.appendChild(autores);

                    Element issn = docX4.createElement("ISSN");
                    issn.setTextContent(ar.getElementsByTagName("ISSN").item(0).getTextContent());
                    rootX4.appendChild(issn);

                    Element periodico = docX4.createElement("PERIODICO");
                    periodico.setTextContent(ar.getElementsByTagName("PERIODICO").item(0).getTextContent());
                    rootX4.appendChild(periodico);

                    Element paginas = docX4.createElement("PAGINAS");
                    paginas.setTextContent(ar.getElementsByTagName("PAGINAS").item(0).getTextContent());
                    rootX4.appendChild(paginas);

                    Element ano = docX4.createElement("ANO");
                    ano.setTextContent(ar.getElementsByTagName("ANO").item(0).getTextContent());
                    rootX4.appendChild(ano);

                    Message<Document> outMsg = new Message<Document>(inMsg);
                    outMsg.setBody(docX4);
                    exchange.output[0].add(outMsg);
                }

                NodeList livros = docX2.getElementsByTagName("LIVROS-PUBLICADOS-OU-ORGANIZADOS");

                for (int i = 0; i < livros.getLength(); i++) {
                    Element lv = (Element) livros.item(i);

                    Document docX5 = XMLHandler.newDocument();

                    Element rootX5 = docX5.createElement("LIVRO-PUBLICADO");
                    docX5.appendChild(rootX5);

                    Element sequencia = docX5.createElement("SEQUENCIA");
                    sequencia.setTextContent(lv.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                    rootX5.appendChild(sequencia);

                    Element tipo = docX5.createElement("TIPO");
                    tipo.setTextContent(lv.getElementsByTagName("TIPO").item(0).getTextContent());
                    rootX5.appendChild(tipo);

                    Element titulo = docX5.createElement("TITULO-DO-LIVRO");
                    titulo.setTextContent(lv.getElementsByTagName("TITULO-DO-LIVRO").item(0).getTextContent());
                    rootX5.appendChild(titulo);

                    Element ano = docX5.createElement("ANO");
                    ano.setTextContent(lv.getElementsByTagName("ANO").item(0).getTextContent());
                    rootX5.appendChild(ano);

                    Element idioma = docX5.createElement("IDIOMA");
                    idioma.setTextContent(lv.getElementsByTagName("IDIOMA").item(0).getTextContent());
                    rootX5.appendChild(idioma);

                    Element isbn = docX5.createElement("ISBN");
                    isbn.setTextContent(lv.getElementsByTagName("ISBN").item(0).getTextContent());
                    rootX5.appendChild(isbn);

                    Element editorac = docX5.createElement("CIDADE-DA-EDITORA");
                    editorac.setTextContent(lv.getElementsByTagName("CIDADE-DA-EDITORA").item(0).getTextContent());
                    rootX5.appendChild(editorac);

                    Element editora = docX5.createElement("EDITORA");
                    editora.setTextContent(lv.getElementsByTagName("NOME-DA-EDITORA").item(0).getTextContent());
                    rootX5.appendChild(editora);

                    Element autores = docX5.createElement("AUTORES");
                    autores.setTextContent(lv.getElementsByTagName("AUTORES").item(0).getTextContent());
                    rootX5.appendChild(autores);

                    Message<Document> outMsg = new Message<Document>(inMsg);
                    outMsg.setBody(docX5);
                    exchange.output[0].add(outMsg);
                }

                NodeList capitulos = docX2.getElementsByTagName("CAPITULO-DE-LIVRO-PUBLICADO");

                for (int i = 0; i < capitulos.getLength(); i++) {
                    Element cap = (Element) capitulos.item(i);

                    Document docX5 = XMLHandler.newDocument();

                    Element rootX5 = docX5.createElement("LIVRO-PUBLICADO");
                    docX5.appendChild(rootX5);

                    Element sequencia = docX5.createElement("SEQUENCIA");
                    sequencia.setTextContent(cap.getElementsByTagName("SEQUENCIA-PRODUCAO").item(0).getTextContent());
                    rootX5.appendChild(sequencia);

                    Element tipo = docX5.createElement("TIPO");
                    tipo.setTextContent(cap.getElementsByTagName("TIPO").item(0).getTextContent());
                    rootX5.appendChild(tipo);

                    Element titulo = docX5.createElement("TITULO-DO-LIVRO");
                    titulo.setTextContent(cap.getElementsByTagName("TITULO-DO-LIVRO").item(0).getTextContent());
                    rootX5.appendChild(titulo);

                    Element ano = docX5.createElement("ANO");
                    ano.setTextContent(cap.getElementsByTagName("ANO").item(0).getTextContent());
                    rootX5.appendChild(ano);

                    Element idioma = docX5.createElement("IDIOMA");
                    idioma.setTextContent(cap.getElementsByTagName("IDIOMA").item(0).getTextContent());
                    rootX5.appendChild(idioma);

                    Element isbn = docX5.createElement("ISBN");
                    isbn.setTextContent(cap.getElementsByTagName("ISBN").item(0).getTextContent());
                    rootX5.appendChild(isbn);

                    Element editorac = docX5.createElement("CIDADE-DA-EDITORA");
                    editorac.setTextContent(cap.getElementsByTagName("CIDADE-DA-EDITORA").item(0).getTextContent());
                    rootX5.appendChild(editorac);

                    Element editora = docX5.createElement("EDITORA");
                    editora.setTextContent(cap.getElementsByTagName("NOME-DA-EDITORA").item(0).getTextContent());
                    rootX5.appendChild(editora);

                    Element autores = docX5.createElement("AUTORES");
                    autores.setTextContent(cap.getElementsByTagName("AUTORES").item(0).getTextContent());
                    rootX5.appendChild(autores);

                    Message<Document> outMsg = new Message<Document>(inMsg);
                    outMsg.setBody(docX5);
                    exchange.output[0].add(outMsg);
                }

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX2);
                exchange.output[0].add(outMsg);
            }
        };
        task[1].input[0].bind(slot[0]);
        task[1].output[0].bind(slot[1]);
        addTask(task[1]);

        // Dispatcher
        task[2] = new Dispatcher("Dispatcher t2", 4) {
            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                Document doc = inMsg.getBody();

                if (doc.getFirstChild().getNodeName().equals("TRABALHO-EM-EVENTOS")) {
                    exchange.output[0].add(inMsg);
                } else if (doc.getFirstChild().getNodeName().equals("ARTIGO-PUBLICADO")) {
                    exchange.output[1].add(inMsg);
                } else if (doc.getFirstChild().getNodeName().equals("LIVRO-PUBLICADO")) {
                    exchange.output[2].add(inMsg);
                } else {
                    exchange.output[3].add(inMsg);
                }
            }
        };
        task[2].input[0].bind(slot[1]);
        task[2].output[0].bind(slot[2]); //Conferências
        task[2].output[1].bind(slot[11]); //Periódicos
        task[2].output[2].bind(slot[9]); //Livros
        task[2].output[3].bind(slot[23]); //docX2 para dados (nome e id)
        addTask(task[2]);

        // Translator //Conferências em eventos
        task[3] = new Translator("Translator t3") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                //Transforma um docX3 adicionando a tag para o Qualis
                Document docX3 = inMsg.getBody();

                Element qualis = docX3.createElement("QUALIS");
                docX3.getFirstChild().appendChild(qualis);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX3);
//                XMLHandler.writeXmlFile(outMsg.getBody(), "saidaTeste.xml");
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

        // Translator Qualis Q1 de Conferência
        task[5] = new Translator("Translator t5") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX3 com a Sequencia pedindo por um valor(fixo)
                // para o Qualis de Eventos
                Document docX3 = inMsg.getBody();
                Document docQ1 = XMLHandler.newDocument();

                Element root = docQ1.createElement("QUALIS-DE-EVENTOS");
                docQ1.appendChild(root);

                Element sequencia = docQ1.createElement("SEQUENCIA");
                sequencia.setTextContent(docX3.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docQ1);
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

                docX3.getElementsByTagName("QUALIS").item(0).setTextContent(docQ1.getElementsByTagName("QUALIS").item(0).getTextContent());
//                XMLHandler.writeXmlFile(inMsg0.getBody(), "saidaTeste.xml");
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

                // Transforma um docX5
                Document docX5 = inMsg.getBody();

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX5);
                exchange.output[0].add(outMsg);
            }
        };
        task[8].input[0].bind(slot[9]);
        task[8].output[0].bind(slot[10]);
        addTask(task[8]);

        // Translator //Periódicos
        task[9] = new Translator("Translator t9") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Transforma um docX4 adicionando as tags Qualis e H-Index
                Document docX4 = inMsg.getBody();

                Element qualis = docX4.createElement("QUALIS");
                docX4.getFirstChild().appendChild(qualis);

                Element hindex = docX4.createElement("H-INDEX");
                docX4.getFirstChild().appendChild(hindex);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docX4);
                exchange.output[0].add(outMsg);
            }
        };
        task[9].input[0].bind(slot[11]);
        task[9].output[0].bind(slot[12]);
        addTask(task[9]);

        // Replicator
        task[10] = new Replicator("Replicator t10", 2);
        task[10].input[0].bind(slot[12]);
        task[10].output[0].bind(slot[13]);
        task[10].output[1].bind(slot[14]);
        addTask(task[10]);

        // Translator Qualis Q2 //Periodicos
        task[11] = new Translator("Translator t11") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX4 com a Sequencia pedindo por um valor(fixo)
                // para o Qualis de Periodicos
                Document docX4 = inMsg.getBody();
                Document docQ2 = XMLHandler.newDocument();

                Element root = docQ2.createElement("QUALIS-DE-ARTIGOS");
                docQ2.appendChild(root);

                Element sequencia = docQ2.createElement("SEQUENCIA");
                sequencia.setTextContent(docX4.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docQ2);
                exchange.output[0].add(outMsg);
            }
        };
        task[11].input[0].bind(slot[13]);
        task[11].output[0].bind(solicitorPortQ2.getInterSlotIn());
        addTask(task[11]);

        // Correlator
        task[12] = new Correlator("Correlator t12", 2, 2) {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg0 = (Message<Document>) exchange.input[0].poll();
                Message<Document> inMsg1 = (Message<Document>) exchange.input[1].poll();

                Document docX4 = inMsg0.getBody();
                String sequenciaX4 = docX4.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                Document docQ2 = inMsg1.getBody();
                String sequenciaQ2 = docQ2.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                if (sequenciaX4.equals(sequenciaQ2)) {
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

                Document docX4 = inMsg0.getBody();
                Document docQ2 = inMsg1.getBody();

                docX4.getElementsByTagName("QUALIS").item(0).setTextContent(docQ2.getElementsByTagName("QUALIS").item(0).getTextContent());
//                XMLHandler.writeXmlFile(inMsg0.getBody(), "saidaTesteP.xml");
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

        // Translator //H-index Periódicos
        task[15] = new Translator("Translator t15") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                // Manda um docX4 com a Sequencia pedindo por um valor(fixo)
                // para o H-Index de Periodicos
                Document docX4 = inMsg.getBody();
                Document docH = XMLHandler.newDocument();

                Element root = docH.createElement("H-INDEX-EM-PERIODICO");
                docH.appendChild(root);

                Element sequencia = docH.createElement("SEQUENCIA");
                sequencia.setTextContent(docX4.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                root.appendChild(sequencia);

                Message<Document> outMsg = new Message<Document>(inMsg);
                outMsg.setBody(docH);
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

                Document docX4 = inMsg0.getBody();
                String sequenciaX4 = docX4.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                Document docH = inMsg1.getBody();
                String sequenciaH = docH.getElementsByTagName("SEQUENCIA").item(0).getTextContent();

                if (sequenciaX4.equals(sequenciaH)) {
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

                Document docX4 = inMsg0.getBody();
                Document docH = inMsg1.getBody();

                docX4.getElementsByTagName("H-INDEX").item(0).setTextContent(docH.getElementsByTagName("H-INDEX").item(0).getTextContent());
//                XMLHandler.writeXmlFile(inMsg0.getBody(), "saidaTesteH.xml");
                exchange.output[0].add(inMsg0);
            }

        };
        task[17].input[0].bind(slot[20]);
        task[17].input[1].bind(slot[21]);
        task[17].output[0].bind(slot[22]);
        addTask(task[17]);

        // Merger
        task[18] = new Merger("Merger t18", 4);
        task[18].input[0].bind(slot[8]);
        task[18].input[1].bind(slot[10]);
        task[18].input[2].bind(slot[22]);
        task[18].input[3].bind(slot[23]);
        task[18].output[0].bind(slot[24]);
        addTask(task[18]);

        // Aggregator
        task[19] = new Aggregator("Aggregator t19") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Document docFinal = XMLHandler.newDocument();

                Element cvlat = docFinal.createElement("CVLattes");
                docFinal.appendChild(cvlat);

                Element producao = docFinal.createElement("PRODUCAO-BIBLIOGRAFICA");
                cvlat.appendChild(producao);

                Element eventos = docFinal.createElement("TRABALHOS-EM-EVENTOS");
                producao.appendChild(eventos);

                Element artigos = docFinal.createElement("ARTIGOS-PUBLICADOS");
                producao.appendChild(artigos);

                Element livros = docFinal.createElement("LIVROS-PUBLICADOS");
                producao.appendChild(livros);

                Message<Document> outMsg = new Message<Document>();
                Document docX = null;
                while (!exchange.input[0].isEmpty()) {
                    //se estiver vazia retorna true, q vai virar false e não rodar o while
                    //se estiver com algum item, vai retornar false q vai virar true e rodar o while
                    Message<Document> msg = (Message<Document>) exchange.input[0].poll();

                    outMsg.addParent(msg);
                    docX = msg.getBody();

                    if (docX.getFirstChild().getNodeName().equals("TRABALHO-EM-EVENTOS")) {
                        Element rootX3 = docFinal.createElement("TRABALHO-EM-EVENTOS");
                        eventos.appendChild(rootX3);

                        Element titulo = docFinal.createElement("TITULO");
                        titulo.setTextContent(docX.getElementsByTagName("TITULO").item(0).getTextContent());
                        rootX3.appendChild(titulo);

                        Element sequencia = docFinal.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        rootX3.appendChild(sequencia);

                        Element autores = docFinal.createElement("AUTORES");
                        autores.setTextContent(docX.getElementsByTagName("AUTORES").item(0).getTextContent());
                        rootX3.appendChild(autores);

                        Element evento = docFinal.createElement("EVENTO");
                        evento.setTextContent(docX.getElementsByTagName("EVENTO").item(0).getTextContent());
                        rootX3.appendChild(evento);

                        Element local = docFinal.createElement("LOCAL");
                        local.setTextContent(docX.getElementsByTagName("LOCAL").item(0).getTextContent());
                        rootX3.appendChild(local);

                        Element anais = docFinal.createElement("ANAIS");
                        anais.setTextContent(docX.getElementsByTagName("ANAIS").item(0).getTextContent());
                        rootX3.appendChild(anais);

                        Element paginas = docFinal.createElement("PAGINAS");
                        paginas.setTextContent(docX.getElementsByTagName("PAGINAS").item(0).getTextContent());
                        rootX3.appendChild(paginas);

                        Element ano = docFinal.createElement("ANO");
                        ano.setTextContent(docX.getElementsByTagName("ANO").item(0).getTextContent());
                        rootX3.appendChild(ano);

                        Element qualis = docFinal.createElement("QUALIS");
                        qualis.setTextContent(docX.getElementsByTagName("QUALIS").item(0).getTextContent());
                        rootX3.appendChild(qualis);

                    } else if (docX.getFirstChild().getNodeName().equals("ARTIGO-PUBLICADO")) {
                        Element rootX4 = docFinal.createElement("ARTIGO-PUBLICADO");
                        artigos.appendChild(rootX4);

                        Element titulo = docFinal.createElement("TITULO");
                        titulo.setTextContent(docX.getElementsByTagName("TITULO").item(0).getTextContent());
                        rootX4.appendChild(titulo);

                        Element sequencia = docFinal.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        rootX4.appendChild(sequencia);

                        Element autores = docFinal.createElement("AUTORES");
                        autores.setTextContent(docX.getElementsByTagName("AUTORES").item(0).getTextContent());
                        rootX4.appendChild(autores);

                        Element issn = docFinal.createElement("ISSN");
                        issn.setTextContent(docX.getElementsByTagName("ISSN").item(0).getTextContent());
                        rootX4.appendChild(issn);

                        Element periodico = docFinal.createElement("PERIODICO");
                        periodico.setTextContent(docX.getElementsByTagName("PERIODICO").item(0).getTextContent());
                        rootX4.appendChild(periodico);

                        Element paginas = docFinal.createElement("PAGINAS");
                        paginas.setTextContent(docX.getElementsByTagName("PAGINAS").item(0).getTextContent());
                        rootX4.appendChild(paginas);

                        Element ano = docFinal.createElement("ANO");
                        ano.setTextContent(docX.getElementsByTagName("ANO").item(0).getTextContent());
                        rootX4.appendChild(ano);

                        Element qualis = docFinal.createElement("QUALIS");
                        qualis.setTextContent(docX.getElementsByTagName("QUALIS").item(0).getTextContent());
                        rootX4.appendChild(qualis);

                        Element hindex = docFinal.createElement("H-INDEX");
                        hindex.setTextContent(docX.getElementsByTagName("H-INDEX").item(0).getTextContent());
                        rootX4.appendChild(hindex);

                    } else if (docX.getFirstChild().getNodeName().equals("LIVRO-PUBLICADO")) {
                        Element rootX5 = docFinal.createElement("LIVRO-PUBLICADO");
                        livros.appendChild(rootX5);

                        Element sequencia = docFinal.createElement("SEQUENCIA");
                        sequencia.setTextContent(docX.getElementsByTagName("SEQUENCIA").item(0).getTextContent());
                        rootX5.appendChild(sequencia);

                        Element tipo = docFinal.createElement("TIPO");
                        tipo.setTextContent(docX.getElementsByTagName("TIPO").item(0).getTextContent());
                        rootX5.appendChild(tipo);

                        Element titulo = docFinal.createElement("TITULO-DO-LIVRO");
                        titulo.setTextContent(docX.getElementsByTagName("TITULO-DO-LIVRO").item(0).getTextContent());
                        rootX5.appendChild(titulo);

                        Element ano = docFinal.createElement("ANO");
                        ano.setTextContent(docX.getElementsByTagName("ANO").item(0).getTextContent());
                        rootX5.appendChild(ano);

                        Element idioma = docFinal.createElement("IDIOMA");
                        idioma.setTextContent(docX.getElementsByTagName("IDIOMA").item(0).getTextContent());
                        rootX5.appendChild(idioma);

                        Element isbn = docFinal.createElement("ISBN");
                        isbn.setTextContent(docX.getElementsByTagName("ISBN").item(0).getTextContent());
                        rootX5.appendChild(isbn);

                        Element editorac = docFinal.createElement("CIDADE-DA-EDITORA");
                        editorac.setTextContent(docX.getElementsByTagName("CIDADE-DA-EDITORA").item(0).getTextContent());
                        rootX5.appendChild(editorac);

                        Element editora = docFinal.createElement("EDITORA");
                        editora.setTextContent(docX.getElementsByTagName("EDITORA").item(0).getTextContent());
                        rootX5.appendChild(editora);

                        Element autores = docFinal.createElement("AUTORES");
                        autores.setTextContent(docX.getElementsByTagName("AUTORES").item(0).getTextContent());
                        rootX5.appendChild(autores);
                    } else {
                        Element id = docFinal.createElement("ID");
                        id.setTextContent(docX.getElementsByTagName("ID").item(0).getTextContent());
                        cvlat.appendChild(id);

                        Element nome = docFinal.createElement("NOME");
                        nome.setTextContent(docX.getElementsByTagName("NOME").item(0).getTextContent());
                        cvlat.appendChild(nome);
                    }
                }

                outMsg.setBody(docFinal);
                exchange.output[0].add(outMsg);
            }

        };
        task[19].input[0].bind(slot[24]);
        task[19].output[0].bind(slot[25]);
        addTask(task[19]);

        // Translator //xml final
        task[20] = new Translator("Translator t20") {

            @Override
            public void doWork(Exchange exchange) throws TaskExecutionException {
                Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

                //Aqui pode ser utilizado para transformar o XML em um documento HTML através de um XSLT
                Document doc = inMsg.getBody();

                System.out.println("Última Task");

                Message<Document> outMsg = new Message<Document>();
                outMsg.setBody(doc);
                exchange.output[0].add(outMsg);
            }
        };
        task[20].input[0].bind(slot[25]);
        task[20].output[0].bind(exitPortCurriculum.getInterSlot());
        addTask(task[20]);

    }

}

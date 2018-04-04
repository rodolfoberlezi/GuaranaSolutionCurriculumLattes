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
import guarana.toolkit.task.routers.Correlator;
import guarana.toolkit.task.routers.Merger;
import guarana.toolkit.task.routers.Replicator;
import guarana.toolkit.task.routers.Filter;
import guarana.toolkit.task.transformers.Assembler;
import guarana.toolkit.task.transformers.Translator;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.net.httpserver.HttpExchange;

import file.xml.XMLHandler;

import com.sun.net.httpserver.Filter.Chain;

public class IntProcess extends Process {

	private Slot[] slot;
	private Task[] task;
	private OneWayPort entryPortCurriculum, exitPortCurriculum;
	private TwoWayPort solicitorPortQ1, solicitorPortQ2, solicitorPortH;
	public InDummyCommunicator communicatorEntry;
	public int curriculunsRead;

	public IntProcess() {
		super("Integration Process");

		slot = new Slot[24];
		for (int i = 0; i < slot.length; i++) {
			slot[i] = new Slot("Slot " + i);
		}

		task = new Task[20];

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

						Document docx = (Document) request.getBody();
						// mensagem XML para ser usada como saída
						// docx.setXmlVersion(CURRICULUM_FILE);

						Message<Document> outMsg = new Message<Document>(request);
						outMsg.setBody(docx);

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
						super.execute();
						curriculunsRead++;
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

				Document docX1 = (Document) inMsg.getBody(); // recebe objeto
																// docX1
																// vindo da
																// classe
																// TestCurriculum

				String curriculumID = docX1.getElementsByTagName("Id").item(0).getTextContent();
				NodeList publications = docX1.getElementsByTagName("Publications");

				for (int i = 0; i < publications.getLength(); i++) {
					Element p = (Element) publications.item(i);

					Document docX2 = XMLHandler.newDocument();

					if (docX1.getElementsByTagName("Article").toString() == "Article") {

						// Article
						Element rootX2 = docX2.createElement("Article");
						docX2.appendChild(rootX2);

						// CurriculumID
						Element id = docX2.createElement("CurriculumID");
						id.setTextContent(curriculumID + "#" + i);
						rootX2.appendChild(id);

						// Authors
						Element authors = docX2.createElement("Authors");
						authors.setTextContent(p.getElementsByTagName("Authors").item(0).getTextContent());
						rootX2.appendChild(authors);

						// Title
						Element title = docX2.createElement("Title");
						title.setTextContent(p.getElementsByTagName("Title").item(0).getTextContent());
						rootX2.appendChild(title);

						// Journal
						Element journal = docX2.createElement("Journal");
						journal.setTextContent(p.getElementsByTagName("Journal").item(0).getTextContent());
						rootX2.appendChild(journal);

						// Year
						Element year = docX2.createElement("Year");
						year.setTextContent(p.getElementsByTagName("Year").item(0).getTextContent());
						rootX2.appendChild(year);

						// Pages
						Element pages = docX2.createElement("Pages");
						pages.setTextContent(p.getElementsByTagName("Pages").item(0).getTextContent());
						rootX2.appendChild(pages);

						// Volume
						Element volume = docX2.createElement("Volume");
						volume.setTextContent(p.getElementsByTagName("Volume").item(0).getTextContent());
						rootX2.appendChild(volume);

					}
					if (docX1.getElementsByTagName("Conference").toString() == "Conference") {

						// Conference
						Element rootX2 = docX2.createElement("Conference");
						docX2.appendChild(rootX2);

						// CurriculumID
						Element id = docX2.createElement("CurriculumID");
						id.setTextContent(curriculumID + "#" + i);
						rootX2.appendChild(id);

						// NameEvent
						Element nameEvent = docX2.createElement("NameEvent");
						nameEvent.setTextContent(p.getElementsByTagName("NameEvent").item(0).getTextContent());
						rootX2.appendChild(nameEvent);

						// Authors
						Element authors = docX2.createElement("Authors");
						authors.setTextContent(p.getElementsByTagName("Authors").item(0).getTextContent());
						rootX2.appendChild(authors);

						// Title
						Element title = docX2.createElement("Title");
						title.setTextContent(p.getElementsByTagName("Title").item(0).getTextContent());
						rootX2.appendChild(title);

						// Year
						Element year = docX2.createElement("Year");
						year.setTextContent(p.getElementsByTagName("Year").item(0).getTextContent());
						rootX2.appendChild(year);

					}
					if (docX1.getElementsByTagName("Book").toString() == "Book") {

						// Book
						Element rootX2 = docX2.createElement("Book");
						docX2.appendChild(rootX2);

						// CurriculumID
						Element id = docX2.createElement("CurriculumID");
						id.setTextContent(curriculumID + "#" + i);
						rootX2.appendChild(id);

						// Authors
						Element authors = docX2.createElement("Authors");
						authors.setTextContent(p.getElementsByTagName("Authors").item(0).getTextContent());
						rootX2.appendChild(authors);

						// Title
						Element title = docX2.createElement("Title");
						title.setTextContent(p.getElementsByTagName("Title").item(0).getTextContent());
						rootX2.appendChild(title);

						// Editor
						Element editor = docX2.createElement("Journal");
						editor.setTextContent(p.getElementsByTagName("Journal").item(0).getTextContent());
						rootX2.appendChild(editor);

						// Year
						Element year = docX2.createElement("Year");
						year.setTextContent(p.getElementsByTagName("Year").item(0).getTextContent());
						rootX2.appendChild(year);

						// Pages
						Element pages = docX2.createElement("Pages");
						pages.setTextContent(p.getElementsByTagName("Pages").item(0).getTextContent());
						rootX2.appendChild(pages);

						// Volume
						Element volume = docX2.createElement("Volume");
						volume.setTextContent(p.getElementsByTagName("Volume").item(0).getTextContent());
						rootX2.appendChild(volume);

					} else {
						TaskExecutionException e;

					}

					Message<Document> outMsg = new Message<Document>(inMsg);
					outMsg.setBody(docX2);
					exchange.output[0].add(outMsg);
				}

			}

		};
		task[0].input[0].bind(entryPortCurriculum.getInterSlot());
		task[0].output[0].bind(slot[0]);
		addTask(task[0]);

		// Translator
		task[1] = new Translator("Translator t1") {

			@Override
			public void doWork(Exchange exchange) throws TaskExecutionException {
				Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

				Document docX2 = inMsg.getBody();
				Document docX3 = XMLHandler.newDocument();

				// QualisConferencRequest
				Element rootX3 = docX3.createElement("QualisConferenceRequest");
				docX3.appendChild(docX3);

				// NameEvent
				Element nameEvent = docX3.createElement("NameEvent");
				nameEvent.setTextContent(docX2.getElementsByTagName("NameEvent").item(0).getTextContent());
				rootX3.appendChild(nameEvent);

				// Authors
				Element authors = docX3.createElement("Authors");
				authors.setTextContent(docX2.getElementsByTagName("Authors").item(0).getTextContent());
				rootX3.appendChild(authors);

				// Title
				Element title = docX3.createElement("Title");
				title.setTextContent(docX2.getElementsByTagName("Title").item(0).getTextContent());
				rootX3.appendChild(title);

				// Year
				Element year = docX3.createElement("Year");
				year.setTextContent(docX2.getElementsByTagName("Year").item(0).getTextContent());
				rootX3.appendChild(year);

				// Qualis
				Element qualis = docX3.createElement("Qualis");
				rootX3.appendChild(qualis);

				Message<Document> outMsg = new Message<Document>(inMsg);
				outMsg.setBody(docX3);
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

		// Translator //Conferências
		task[3] = new Translator("Translator t3") {

			@Override
			public void doWork(Exchange exchange) throws TaskExecutionException {
				Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

				Document docX2 = inMsg.getBody();
				Document docX3 = XMLHandler.newDocument();

				// QualisConferencRequest
				Element rootX3 = docX3.createElement("QualisConferenceRequest");
				docX3.appendChild(docX3);

				// NameEvent
				Element nameEvent = docX3.createElement("NameEvent");
				nameEvent.setTextContent(docX2.getElementsByTagName("NameEvent").item(0).getTextContent());
				rootX3.appendChild(nameEvent);

				// Authors
				Element authors = docX3.createElement("Authors");
				authors.setTextContent(docX2.getElementsByTagName("Authors").item(0).getTextContent());
				rootX3.appendChild(authors);

				// Title
				Element title = docX3.createElement("Title");
				title.setTextContent(docX2.getElementsByTagName("Title").item(0).getTextContent());
				rootX3.appendChild(title);

				// Year
				Element year = docX3.createElement("Year");
				year.setTextContent(docX2.getElementsByTagName("Year").item(0).getTextContent());
				rootX3.appendChild(year);

				// Qualis
				Element qualis = docX3.createElement("Qualis");
				rootX3.appendChild(qualis);

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

		// Translator Qualis Q1 de Conferência
		task[5] = new Translator("Translator t5") {

			@Override
			public void doWork(Exchange exchange) throws TaskExecutionException {
				Message<Document> inMsg = (Message<Document>) exchange.input[0].poll();

				Document docX3 = inMsg.getBody();
				Document docX4 = XMLHandler.newDocument();

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
			public void doWork(Exchange arg0) throws TaskExecutionException {

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
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}

		};
		task[7].input[0].bind(slot[6]);
		task[7].input[1].bind(slot[7]);
		task[7].output[0].bind(slot[8]);
		addTask(task[7]);

		// Translator //Livros
		task[8] = new Translator("Translator t8") {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}
		};
		task[8].input[0].bind(slot[9]);
		task[8].output[0].bind(slot[10]);
		addTask(task[8]);

		// Translator //Periódicos
		task[9] = new Translator("Translator t9") {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

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

		// Translator Qualis Q2 //Periódicos
		task[11] = new Translator("Translator t11") {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}
		};
		task[11].input[0].bind(slot[14]);
		task[11].output[0].bind(solicitorPortQ2.getInterSlotIn());
		addTask(task[11]);

		// Correlator
		task[12] = new Correlator("Correlator t12", 2, 2) {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

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
			public void doWork(Exchange arg0) throws TaskExecutionException {

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

		// Translator //H-index periódicos
		task[15] = new Translator("Translator t15") {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}
		};
		task[15].input[0].bind(slot[18]);
		task[15].output[0].bind(solicitorPortH.getInterSlotIn());
		addTask(task[15]);

		// Correlator
		task[16] = new Correlator("Correlator t16", 2, 2) {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

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
			public void doWork(Exchange arg0) throws TaskExecutionException {

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
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}

		};
		task[19].input[0].bind(slot[23]);
		task[19].output[0].bind(slot[24]);
		addTask(task[19]);

		// Translator //xml final
		task[20] = new Translator("Translator t20") {

			@Override
			public void doWork(Exchange arg0) throws TaskExecutionException {

			}
		};
		task[20].input[0].bind(slot[24]);
		task[20].output[0].bind(exitPortCurriculum.getInterSlot());
		addTask(task[20]);

	}

}
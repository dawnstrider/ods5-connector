package net.sf.dawnstrider.odsconnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.asam.ods.AoSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.serviceloader.ServiceListFactoryBean;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import net.sf.dawnstrider.odsconnector.ifs.ODSAction;

@SpringBootApplication
public class OdsConnectorApplication implements CommandLineRunner {

	private static final String OPTION_NSP = "NameServicePort";
	private static final String OPTION_NSH = "NameServiceHost";

	private ApplicationContext ctx;

	private ConsoleWrapper console;

	@Autowired
	ServiceListFactoryBean svcFactory;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OdsConnectorApplication.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

	public OdsConnectorApplication(@Autowired ApplicationContext ctx, @Autowired ConsoleWrapper console) {
		this.ctx = ctx;
		this.console = console;
	}

	@Override
	public void run(String... args) throws Exception {

		printTopic();

		Options opts = getOptions();

		DefaultParser p = new DefaultParser();
		CommandLine cmd = p.parse(opts, args);

		if (cmd.hasOption(OPTION_NSH) && cmd.hasOption(OPTION_NSP)) {

		} else {

			String line = console.readLine("Please enter the hostname of the NameService:", "localhost");

			String host = line;

			line = console.readLine("Please enter the hostname of the NameService:", "2809");

			Integer port = Integer.valueOf(line);

			line = console.readLine("Is this a connection using SSL?:", "false");

			Boolean isSSL = Boolean.valueOf(line);

			Connector c = new Connector(host, port, isSSL);

			String[] services = c.listServices();

			console.printLine("Connected to NameService");
			console.printLine("Please select the service you want to connect to:");
			for (int i = 0; i < services.length; i++) {
				console.printLine("[" + i + "] " + services[i]);
			}
			line = console.readLine();

			Short server = Short.valueOf(line);

			line = console.readLine("Please enter a username: ");
			String user = line;

			line = console.readPassword("Please enter a password: ");
			String pw = line;

			AoSession session = c.connect(services[server], user, pw);

			try {
				List<ODSAction> actions = (List<ODSAction>) svcFactory.getObject();
				HashMap<Integer, ODSAction> actionMap = new HashMap<Integer, ODSAction>();
				for (int i = 1; i <= actions.size(); i++) {
					actionMap.put(i, actions.get(i - 1));
				}

				Integer selection = 0;
				do {
					console.printLine("");
					console.printLine("Please select an action you would like to execute:");
					console.printLine("[0] Exit the application");
					actionMap.forEach((i, a) -> console.printLine("[" + i + "] " + a.getDescription()));

					line = console.readLine();
					selection = Integer.valueOf(line);

					try {
						runAction(selection, actionMap, session);
					} catch (ExecutionException e) {
						System.err.println("Could not execute the action, check the logs for details");
						throw e;
					}

				} while (selection > 0);

			} finally {
				session.close();
			}

		}

	}

	private void printTopic() {
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("git.properties")) {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int b = in.read();
			while (b >= 0) {
				bos.write(b);
				b = in.read();
			}

			console.printLine("ODS Connector Utiliy");

			JSONObject json = new JSONObject(bos.toString());

			console.printLine("Version: " + json.getString("git.build.version"));
			console.printLine("Git ID: " + json.getString("git.commit.id"));

			boolean dev;
			if (json.getString("git.tags").compareTo("") == 0) {
				dev = true;
			} else {
				dev = false;
			}
			if (dev) {
				console.printLine("!!!!!!!!!!!Development Version!!!!!!!!!!");
				console.printLine("!!!!!!!!!!!Development Version!!!!!!!!!!");
				console.printLine("!!!!!!!!!!!Development Version!!!!!!!!!!");
				console.printLine("UNCOMMITTED CHANGES: " + json.getString("git.dirty"));
			}

			console.printLine("----------");
			console.printLine("");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void runAction(Integer selection, HashMap<Integer, ODSAction> actionMap, AoSession session)
			throws ExecutionException {

		ODSAction action = actionMap.get(selection);
		if (action != null) {
			action.execute(session, console);
		}
	}

	@Bean
	public ServiceListFactoryBean serviceListFactoryBean() {
		ServiceListFactoryBean serviceListFactoryBean = new ServiceListFactoryBean();
		serviceListFactoryBean.setServiceType(ODSAction.class);
		return serviceListFactoryBean;
	}

	private Options getOptions() {
		Options ret = new Options();

		Option o = new Option(OPTION_NSH, "Hostname where the NameService is running");
		o.setArgs(1);
		ret.addOption(o);

		o = new Option(OPTION_NSP, "Portnumber of the host where the Nameservice is running");
		o.setArgs(1);

		return ret;
	}

}

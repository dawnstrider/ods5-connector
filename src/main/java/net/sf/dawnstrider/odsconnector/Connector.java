package net.sf.dawnstrider.odsconnector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.asam.ods.AoException;
import org.asam.ods.AoFactory;
import org.asam.ods.AoFactoryHelper;
import org.asam.ods.AoSession;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class Connector {

	private Boolean isSSL;
	private Integer port;
	private String host;
	private ORB orb;
	private ConsoleWrapper console;

	public Connector(String host, Integer port, Boolean isSSL, ConsoleWrapper console) {
		this.host = host;
		this.port = port;
		this.isSSL = isSSL;
		this.console = console;
	}

	public String[] listServices() throws InvalidName, UnknownHostException, IOException {

		ORB orb = getORB();
		Object nsObj = orb.resolve_initial_references("NameService");
		NamingContextExt ns = NamingContextExtHelper.narrow(nsObj);
		console.printLine("Connected to NameService");
		console.printLine("Scanning for entries..");

		BindingIteratorHolder bi = new BindingIteratorHolder();
		BindingListHolder bl = new BindingListHolder();
		ns.list(1000, bl, bi);

		Binding[] values = bl.value;
		ArrayList<NameComponent[]> ret = new ArrayList<NameComponent[]>();

		for (int i = 0; i < values.length; i++) {
			if (values[i].binding_type.value() == BindingType._nobject) {
				ret.add(values[i].binding_name);
			}
		}

		List<String> names = ret.stream().map(nc -> {
			try {
				return ns.to_string(nc);
			} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		return names.toArray(new String[0]);

	}

	public AoSession connect(String service, String user, String password) throws UnknownHostException, IOException, InvalidName, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName, AoException {
		ORB orb = getORB();
		Object nsObj = orb.resolve_initial_references("NameService");
		NamingContextExt ns = NamingContextExtHelper.narrow(nsObj);
		Object obj = ns.resolve_str(service);
		AoFactory aoFac = AoFactoryHelper.narrow(obj);
		return aoFac.newSession("USER=" + user + ",PASSWORD=" + password);

	}

	private ORB getORB() throws UnknownHostException, IOException {
		if (orb == null) {
			InetAddress.getByName(host).isReachable(1_000);
			String[] args = new String[2];
			args[0] = "-ORBInitRef";
			args[1] = "NameService=corbaloc:iiop:1.2@" + host + ":" + port + "/NameService";
			Properties p = new Properties();
			p.setProperty("ORBInitRef", "NameService=corbaloc:iiop:1.2@" + host + ":" + port + "/NameService");
			orb = ORB.init(args, p);
		}
		return orb;
	}

}

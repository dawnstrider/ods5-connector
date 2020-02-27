package net.sf.dawnstrider.odsconnector.actions;

import java.util.concurrent.ExecutionException;

import org.asam.ods.AoSession;

import net.sf.dawnstrider.odsconnector.ConsoleWrapper;
import net.sf.dawnstrider.odsconnector.ifs.ODSAction;

public class PrintIORAction implements ODSAction {

	@Override
	public String getDescription() {
		return "Prints the IOR of the current session";
	}

	@Override
	public void execute(AoSession session, ConsoleWrapper console) throws ExecutionException {
		console.printLine(session.toString());
	}

}

package net.sf.dawnstrider.odsconnector.actions;

import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.asam.ods.AoException;
import org.asam.ods.AoSession;

import net.sf.dawnstrider.odsconnector.ConsoleWrapper;
import net.sf.dawnstrider.odsconnector.ifs.ODSAction;

public class TopLevelElementsAction implements ODSAction {

	@Override
	public String getDescription() {
		return "Returns a list of all top level Elements";
	}

	@Override
	public void execute(AoSession session, ConsoleWrapper console) throws ExecutionException{

		try {
			String[] elements = session.getApplicationStructure().listTopLevelElements("*");
			Stream.of(elements).forEach(e -> console.printLine(e));
		} catch (AoException e) {
			throw new ExecutionException(e.reason, e);
		}

	}

}

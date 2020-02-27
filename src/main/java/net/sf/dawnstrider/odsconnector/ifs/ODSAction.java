package net.sf.dawnstrider.odsconnector.ifs;

import java.util.concurrent.ExecutionException;

import org.asam.ods.AoSession;

import net.sf.dawnstrider.odsconnector.ConsoleWrapper;

public interface ODSAction {

	String getDescription();
	
	void execute(AoSession session, ConsoleWrapper console) throws ExecutionException;
}

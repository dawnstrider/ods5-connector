package net.sf.dawnstrider.odsconnector.actions;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.asam.ods.AoException;
import org.asam.ods.AoSession;
import org.slf4j.LoggerFactory;

import net.sf.dawnstrider.odsconnector.ConsoleWrapper;
import net.sf.dawnstrider.odsconnector.ifs.ODSAction;

public class SetSessionContextVariable implements ODSAction {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger("ODSConnector");
  
  @Override
  public String getDescription() {
    return "Set a variable in the ODS session";
  }

  @Override
  public void execute(AoSession session, ConsoleWrapper console) throws ExecutionException {
    try {
      String varName = console.readLine("Provide the name of the variable:");
      String varValue = console.readLine("Provide the value of the variable:");
      
      session.setContextString(varName, varValue);
    } catch (IOException e) {
      console.printLine("Problem while reading data from console");
      logger.error("Problem while reading data from console", e);
    } catch (AoException e) {
      console.printLine("Problem while setting the variable: "+e.reason);
      logger.error("Problem while reading data from console"+e.reason, e);
    }

  }

}

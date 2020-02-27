package net.sf.dawnstrider.odsconnector.actions;

import java.util.concurrent.ExecutionException;

import org.asam.ods.AoException;
import org.asam.ods.AoSession;
import org.asam.ods.DataType;
import org.asam.ods.NameValue;
import org.asam.ods.NameValueIterator;
import org.asam.ods.TS_Union;
import org.asam.ods.T_LONGLONG;

import net.sf.dawnstrider.odsconnector.ConsoleWrapper;
import net.sf.dawnstrider.odsconnector.ifs.ODSAction;

public class PrintSessionContextAction implements ODSAction {

	@Override
	public String getDescription() {
		return "Prints all the context variables of the session";
	}

	@Override
	public void execute(AoSession session, ConsoleWrapper console) throws ExecutionException {

		try {
			NameValueIterator ctxVars = session.getContext("*");
			int count = ctxVars.getCount();
			NameValue[] vars = ctxVars.nextN(count);
			for (NameValue nameValue : vars) {
				console.printLine("Variable: "+nameValue.valName+", Value: "+toString(nameValue.value.u)+", Flag: "+nameValue.value.flag);
			}
			
		} catch (AoException e) {
			throw new ExecutionException(e.reason, e);
		}

	}

	private String toString(TS_Union u) {
		DataType dt = u.discriminator();
		switch (dt.value()) {
		case DataType._DT_STRING:
			return u.stringVal();
		case DataType._DT_BOOLEAN:
			return Boolean.toString(u.booleanVal());
		case DataType._DT_BYTE:
			return Byte.toString(u.byteVal());
		case DataType._DT_FLOAT:
			return Float.toString(u.floatVal());
		case DataType._DT_LONG:
			return Long.toString(u.longVal());
		case DataType._DT_DOUBLE:
			return Double.toString(u.doubleVal());
		case DataType._DT_LONGLONG:
			return Long.toString(toLong(u.longlongVal()));
		default:
			break;
		}
		return null;
	}

	private long toLong(T_LONGLONG longlongVal) {
		long ret = 0;
		ret = ((ret+1) << 32) + longlongVal.high;
		ret = ret + longlongVal.low;
		return ret;
	}
	
	public static void main(String[] args) {
		PrintSessionContextAction a = new PrintSessionContextAction();
		a.toLong(new T_LONGLONG(1, 5));
	}

}

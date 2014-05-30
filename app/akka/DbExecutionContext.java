package akka;

import play.libs.Akka;
import scala.concurrent.ExecutionContext;

/**
 * Class that contains the ExecutionContext for the akka dispather. Dispatcher
 * is configured in application.conf
 * 
 * @author Anders
 * 
 */
public class DbExecutionContext {
	public final static ExecutionContext	ctx	= Akka.system().dispatchers()
														.lookup("akka.db-dispatcher");
}
package repositories

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object DatabaseExecutionContext {
  implicit lazy val databaseExecutionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
}

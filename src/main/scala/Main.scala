package edu.learning

import slick.jdbc.PostgresProfile.api._

import java.time.LocalDate
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object PrivateExectionContext {
  val executor = Executors.newFixedThreadPool(4)
  implicit val executionContext = ExecutionContext.fromExecutorService(executor)
}

object Main {

  import PrivateExectionContext._

  val shawshankRedemption = Movie(1L, "The Shawshank Redemption", LocalDate.of(1994, 9, 23), 162)
  val matrix = Movie(2L, "The Matrix", LocalDate.of(1999, 3, 31), 134)

  def demoInsertMovie(): Unit = {
    val queryDescription = SlickTables.movieTable += matrix
    val futureId = Connection.db.run(queryDescription)
    futureId.onComplete {
      case Success(newMovieId) => println(s"Query was successful, new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }
    Thread.sleep(10000)
  }

  def demoReadAllMovies():Unit = {
    val resultFuture = Connection.db.run(SlickTables.movieTable.result)
    resultFuture.onComplete{
      case Success(movies) => println(s"Fetched: ${movies.mkString(",")}")
      case Failure(exception) => println(s"Fetching Failed: $exception")
    }
    Thread.sleep(10000)
  }

  def demoReadSomeMovies(): Unit = {
    val resultFuture = Connection.db.run(SlickTables.movieTable.filter(_.name.like("%Matrix%")).result)
    resultFuture.onComplete {
      case Success(movies) => println(s"Fetched: ${movies.mkString(",")}")
      case Failure(exception) => println(s"Fetching Failed: $exception")
    }
    Thread.sleep(10000)
  }

  def demoUpdate():Unit = {
    val queryDescriptor = SlickTables.movieTable.filter(_.id === 1L).update(shawshankRedemption.copy(lengthInMin = 150))
    val futureId = Connection.db.run(queryDescriptor)
    futureId.onComplete {
      case Success(newMovieId) => println(s"Query was successful, new id is $newMovieId")
      case Failure(exception) => println(s"Query failed, reason: $exception")
    }
    Thread.sleep(10000)
  }

  def demoDelete():Unit = {
    Connection.db.run(SlickTables.movieTable.filter(_.id === 1L).delete)
  }

  def main(args: Array[String]): Unit = {
    demoReadSomeMovies()
    println("Hello world!")
    System.exit(0)
  }
}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

type Pend[T] = () => T

class LazyFuture[A](computation: Pend[A]) {

  def run()(implicit ec: ExecutionContext): Future[A] =
    Future(computation())

  def map[B](f: A => B): LazyFuture[Pend[B]] =
    LazyFuture(() => f(computation()))

  def flatMap[B](f: A => LazyFuture[Pend[B]]): LazyFuture[Pend[B]] =
    f(computation())

}

object LazyFuture {
  def apply[A](computation: => A): LazyFuture[A] = new LazyFuture(() => computation)
}


def process(int: Int): LazyFuture[Unit] = LazyFuture {
  Thread.sleep(5000)
  println("executed" + int)
}

val process1 = process(1)
val process2 = process(2)
val process3 = process(3)


val result: LazyFuture[Pend[Unit]] = for {
  _ <- process1
  _ <- process2
  _ <- process3
} yield {
  println("all executed By LazyFuture flatMap")
}

// 使用例
implicit val ec = scala.concurrent.ExecutionContext.global
Await.result(result.run(), Duration.Inf)


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

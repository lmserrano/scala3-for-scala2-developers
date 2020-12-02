/**
 * EXTENSION METHODS
 * 
 * Scala 3 brings first-class support for "extension methods", which allow adding methods to 
 * classes after their definition. Previously, this feature was emulated using implicits.
 */
object ext_methods:

  import scala.concurrent.Future
  import concurrent.ExecutionContext.Implicits.global

  // def zip[A, B](l: Future[A], r: Future[B]): Future[(A, B)] =
  //   l.flatMap(l => r.map(r => (l, r)))

  // // We could call zip with two futures l and r:
  //zip(l, r)

  // // This would be more idiomatic and desirable and natural...
  //l.zip(r)

  // This is a pervasive problem in all Object Oriented Languages...

  // In other languages, like C#, we use extension methods... Now we have them in Scala 3 too!

  // // In Scala 2 we would do an implicit conversion like this:
  // class FutureOps[A](self: Future[A]) {
  //   def zip[B](r: Future[B]): Future[(A, B)] =
  //     self.flatMap(l => r.map(r => (l, r)))
  // }
  // implicit def ToFutureOps[A](future: Future[A]): FutureOps[A] = // This is an implicit conversion in Scala 2
  //   new FutureOps(future)

  // def l: Future[Int] = ???
  // def r: Future[String] = ???

  // l.zip(r)
  // // FutureSyntax

  // // This was one of the 2 use caes for implicits
  // // But this was so common that we would use implicit classes for auto-wrapping behavior
  // implicit class FutureOps[A](self: Future[A]) { // extends AnyVal // ... This is broken... very far away from the problem we're trying to solve, which is adding methods to existing classes
  //   def zip[B](r: Future[B]): Future[(A, B)] =
  //     self.flatMap(l => r.map(r => (l, r)))
  // }

  // In reality, we just want to say: "Hey, Scala, please add these methods to this data type..."

  // Now in Scala 3... The "extension" keyword
  // "extension" + Type parameters that will appear on either part of the expression, Then the Data type you want to extend (and the name associated with that data type, which you can refer to in the definition of the extension method)
  // This method decoration doesn't allow type parameters. They need to be shifted all the way to the start of the extension method definition.
  extension [A, B] (self: Future[A]) def zip(r: Future[B]): Future[(A, B)] =
    self.flatMap(l => r.map(r => (l, r)))

  def l: Future[Int] = ???
  def r: Future[String] = ???

  l.zip(r)

  // This is great... and I can use it, within that scope
  // To use it outside the scope, I need to import these extension methods

  // FutureSyntax

  final case class Email(value: String)

  //(???: scala.concurrent.Future[Int]).zip // It exists. Scala will always call the original and not our own unless we'd import it or be in its scope.

  /**
   * EXERCISE 1
   * 
   * Add an extension method to `Email` to retrieve the username of the email address (the part 
   * of the string before the `@` symbol).
   */
  extension (e: Email) def username: String = e.value.takeWhile(_ != '@')

  val sherlock = Email("sherlock@holmes.com").username

  /**
   * EXERCISE 2
   * 
   * Add an extension method to `Email` to retrieve the server of the email address (the part of 
   * the string after the `@` symbol).
   */
  // extension
  extension (e: Email) def server: String = e.value.dropWhile(_ != '@').tail

  /**
   * EXERCISE 3
   * 
   * Add an extension method to `Option[A]` that can zip one option with another `Option[B]`, to 
   * return an `Option[(A, B)]`.
   */
  // extension
  extension [A, B] (opt: Option[A]):
    def zip(that: Option[B]): Option[(A, B)] =
      for
        a <- opt
        b <- that
      yield (a, b)

  // Finally we can now zip options together and get back an option

  /**
   * A rational number is one in the form n/m, where n and m are integers.
   */
  final case class Rational(numerator: BigInt, denominator: BigInt)

  /**
   * EXERCISE 4
   * 
   * Add a collection of extension methods to `Rational`, including `+`, to add two rational 
   * numbers, `*`, to multiply two rational numbers, and `-`, to subtract one rational number 
   * from another rational number.
   */
  // extension

  // Let's add a collection of extension methods to the same type
  object rational_extensions: // We've decided to put them all under a single object so that we can just import it if we want to import all these extension methods
    extension (self: Rational):
      def * (that:Rational): Rational = Rational(that.numerator * self.numerator, self.denominator * that.denominator)
      def + (that: Rational): Rational = Rational(that.denominator * self.numerator + self.denominator * that.numerator , self.denominator * that.numerator)
      def - (that: Rational): Rational = Rational(that.denominator * self.numerator - self.denominator * that.numerator,  self.denominator * that.numerator)

  /**
   * EXERCISE 5
   * 
   * Convert this implicit syntax class to use extension methods.
   */
  // implicit class StringOps(self: String):
  //   def equalsIgnoreCase(that: String) = self.toLowerCase == that.toLowerCase

  // object scope:
  //   extension (s: String) def isSherlock: Boolean = s.startsWith("Sherlock")

  object scope:
    extension (self: String): // also may work with `extension (self: => String)` // Being lazy on the left side
      def equalsIgnoreCase(that: String) = self.toLowerCase == that.toLowerCase
      def isSherlock: Boolean = self.startsWith("Sherlock")

  /**
   * EXERCISE 6
   * 
   * Import the extension method `isSherlock` into the following object so the code will compile.
   */
  object test:
    // "John Watson".isSherlock
    import scope._

    "John Watson".isSherlock

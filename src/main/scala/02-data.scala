/**
 * ENUMS
 * 
 * Scala 3 adds support for "enums", which are to sealed traits like case classes 
 * were to classes. That is, enums cut down on the boilerplate required to use 
 * the "sealed trait" pattern for modeling so-called sum types, in a fashion very 
 * similar to how case classes cut down on the boilerplate required to use 
 * classes to model so-called product types.
 * 
 * Strictly speaking, Scala 3 enums are not the same as Java enums: while the 
 * constructors of enums are finite, and defined statically at compile-time in the 
 * same file, these constructors may have parameters, and therefore, the total 
 * number of values of any enum type could be large or infinite.
 * 
 * Enums and case classes provide first-class support for "algebraic data types" 
 * in Scala 3.
 */
package enums:
  final case class Person(name: String, age: Int) // a Record. Maps to relational database tables. Columns are fields. Tables can model all kinds of things.
  // If we generalize a field, we don't call it a field. We call it a "term". This record contains 2 terms.

  // Sumtypes in Functional Programming -> Records with 1 term
  // Example: PaymentType - Wiretransfer, Bank Transfer and Credit Card
  // They are all different. Require different information, and you choose only one when you want to pay.
  // Records are not the best for this, but a SumType is.
  // Records are ProductTypes. Opposite of SumType.
  //
  // ProductTypes model AND (this term, and this term)
  // SumTypes model OR (or this term, or this term)

  //Tuple2[String, Int] // A ProductType - Contains both a String and an Int - Products contain all their terms
  //Either[String, Int] // A SumType - Contains a String OR an Int - Sums contain exactly 1 of their terms

  // Scala 2 has pretty powerful support for ProductTypes. Case Classes. That's how you model classes in Scala 2.
  // Scala 3 created `enum` to do for SumTypes what case classes do for ProductTypes

  enum FavoriteIDE:
    case VSCode(version: Int)
    case IDEA(majorVersion: Int, minorVersion: Int)
    case Vim
    case Emacs

  val favIde: FavoriteIDE = FavoriteIDE.VSCode(13)

  // The compiler will force us to handle all these cases in the `match`
  def example =
    favIde match
      case FavoriteIDE.VSCode(v)  => println(s"You like VS Code ${v}")
      case FavoriteIDE.IDEA(v, _) => println(s"You like IntelliJ IDEA ${v}")
      case FavoriteIDE.Vim        => println(s"You like Vim!")
      case FavoriteIDE.Emacs      => println(s"You like Emacs!")

  // For case classes, we get equals, hashCode, toString, for free
  // In a similar fashion, all the cases of the enum, have hashCode and toString for free too.
  // In fact, each case of the enum can be regarded as a case class that extends the base type
  // We no longer need to use `sealed trait`s to do this.

  // In Scala 3
  // Types should always start with an uppercase letter
  // Values should always start with a lowercase letter

  // Important note:
  // You can only enumerate the values of the enum IF all your cases have no parameters.
  // The moment you add a single parameter to one/any of them, you lose the ability to list them!

  // Let's improve on the previous example. Now all will need to have `version` defined:

  enum FavoriteIDE2:
    def version: Int
    case VSCode(version: Int)
    case IDEA(version: Int, minorVersion: Int)

    // But note that we can't match against phantom type parameters and that these 2 would just be a inner definition, rather than part of the enum
    case object Vim {
      def version = 2
    }
    case object Emacs {
      def version = 1
    }

  val favIde2 = FavoriteIDE2.VSCode(2)
  //val favIde2Not = FavoriteIDE2.Vim // This would give an error in this example!!!
  
  // ----

  /**
   * EXERCISE 1
   * 
   * Convert this "sealed trait" to an enum.
   */
  enum DayOfWeek:
    case Sunday
    case Monday
    case Tuesday
    case Wednesday
    case Thursday
    case Friday
    case Saturday

  /**
   * EXERCISE 2
   * 
   * Explore interop with Java enums by finding all values of `DayOfWeek`, and by 
   * finding the value corresponding to the string "Sunday".
   */
  def daysOfWeek: Array[DayOfWeek] = DayOfWeek.values // Java Interop. Only because we don't have any parameters in the enum cases. Adding a single parameter to one of them this would go away.
  def sunday: DayOfWeek = DayOfWeek.valueOf("Sunday")

  // ----

  /*
  pagoda_5bToday at 12:49 PM
  if you look here: http://dotty.epfl.ch/docs/reference/enums/enums.html#implementation
  I guess you can figure out that enum cases can't have custom methods because they're all actually implemented as individual instances of the super trait... not specific classes
  DayOfWeek.valueOf("don't treat me like this... @_@")

  :cry:
  jarek000000Today at 12:52 PM
  interesting that name() does not work:
  println(enums.DayOfWeek.Sunday.name()
  */
  // // This seems to be a bug, since name() appears not to exist
  //println(enums.DayOfWeek.Sunday.name()
  // ----

  /**
   * EXERCISE 3
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type of any of the case constructors!
   */
  enum Color:
    case Red
    case Green
    case Blue
    case Custom(red: Int, green: Int, blue: Int)

  // // With this, the type of Custom is Color! This is a significant and inumerously important difference
  // // We will always upcast the result to Color when using enum

  //val custom: Color.Custom = Color.Custom(12, 123, 123)
  val custom = Color.Custom(12, 123, 123)

  def methods(c: Color.Custom) = ???

  // sealed trait Color 
  // object Color:
  //   case object Red extends Color 
  //   case object Green extends Color 
  //   case object Blue extends Color
  //   final case class Custom(red: Int, green: Int, blue: Int) extends Color
  //
  // // With this, the type of Custom would be Custom
  //
  //val custom: Color = Color.Custom(123, 123, 123)
  //
  // // Why is this difference important, between the sealed and new enum approach? Because of type inference!
  //
  // // This wouldn't compile, and have a weird error message
  // def ex = List(None)
  // // Scala would infer the type of our accumulator. Which is not exactly correct because although it started correct, we want it to broaden up thoughout time. Like, a None to a Some(...)
  // // In Scala 2 we have bad type inference sometimes and a bad experience. Scala 3 intends to improve on this, hence why the enum approach too.
  // def ex2 = List(1, 2, 3).foldLeft(None) {
  //   case (none, i) if i ==2 => Some(i)
  //   case (some, _) => some
  // }

  // Really helps with polymorphic methods

  // Like this, it compiles
  def ex2 = List(1, 2, 3).foldLeft(None: Option[Int]) {
    case (none, i) if i ==2 => Some(i)
    case (some, _) => some
  }

  val a1 = List.empty // Nil is not the same. List.empty is polymorphic. When you use Nil you get back Option[Nothing]
  val a2 = Option.empty // None is not the same. Same thing here...

  val a3 = List.empty[Int]
  val a4 = Option.empty[String]

  // We get something of type Color, regardless if Red, or Custom, or anything else
  // => Improved user experience!
  def ex3 = List(1, 2, 3).foldLeft(Color.Red) {
    case (Color.Red, i) if i ==2 => Color.Custom(i, i, i)
    case (some, _) => some
  }

  /**
   * EXERCISE 4
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   */

  // // Polymorphic SumType
  // // This is a mess!...
  // // In Haskell it would be:
  // // data Result e v = Succeed v | Fail e
  // // but in Scala 2 ...:
  // sealed trait Result[+Error, +Value]
  // object Result:
  //   final case class Succeed[Value](value: Value) extends Result[Nothing, Value]
  //   final case class Fail[Error](error: Error) extends Result[Error, Nothing]

  enum Result[+Error, +Value]:
    case Success(value: Value)
    case Fail(error: Error)

  // val result = Result.Succeed(12) // The inferred type of Result will be... Result[Nothing, Int]
  // val resultFailed = Fail("Error") //

  // Left[Int, String] // For historical reasons takes 2 type parameters...
  // Left(12) // Scala will often infer Nothing, and that is correct, although it will not always happen

  // val result2 = new Result.Succeed[Int, Int](12)
  // val result3 = new Result.Succeed[Int, Int](12)


  /* // We can also use it with a Phantom `R`
  enum Result[-R, +E, +A]:
    case Succeed(value: A)
    case Fail(error: E)
  */

  /**
   * EXERCISE 5
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   */
  // sealed trait Workflow[-Input, +Output]
  // object Workflow:
  //   final case class End[Output](value: Output) extends Workflow[Any, Output]

  enum Workflow[-Input, +Output]:
    case End(value: Output)

  /**
   * EXERCISE 6
   * 
   * Convert this "sealed trait" to an enum.
   */
  // sealed trait Conversion[-From, +To]
  // object Conversion:
  //   case object AnyToString extends Conversion[Any, String]
  //   case object StringToInt extends Conversion[String, Option[Int]]

  // Polymorphic ADT and Polymorphic enum
  // //This compiles, but is not what we want (with the commented extends)
  // enum Conversion[-From, +To]:
  //   case AnyToString // extends Conversion[Any, String]
  //   case StringToInt // extends Conversion[String, Option[Int]]
  //
  //val a = Conversion.AnyToString // enums.Conversion[Any, Nothing] // This is not what we want!... contravariant and covariant, is why Any and Nothing... not the same as 

  //This compiles, but is not what we want (with the commented extends)
  enum Conversion[-From, +To]:
    case AnyToString extends Conversion[Any, String]
    case StringToInt extends Conversion[String, Option[Int]]

  val a = Conversion.AnyToString // enums.Conversion[Any, String] // This is what we want :)


/**
 * CASE CLASSES
 * 
 * Scala 3 makes a number of improvements to case classes.
 */
package case_classes:
  /**
   * EXERCISE 1
   * 
   * By making the public constructor private, make a smart constructor for `Email` so that only 
   * valid emails may be created.
   */
  // final case class Email(value: String)
  // object Email:
  //   def fromString(v: String): Option[Email] = ???

  //   def isValidEmail(v: String): Boolean = v.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")

  // We will make the constructor private, so that only Email and the companion object can use it.
  // This technique is called private(?) constructors
  final case class Email private (value: String)
  object Email:
    // def apply(value: String): Email = new Email(value) // because of "case class", Scala 2 gives us this for free
    // private def apply(value: String): Email = new Email(value) // because of "case class", Scala 2 gives us this for free. We would make it private to force disable external usage

    def fromString(v: String): Option[Email] =
      if isValidEmail(v) then Some(Email(v)) else None

    def isValidEmail(v: String): Boolean = v.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")

  // With this "private", we no longer have access to a "copy" method on Email too.

  /**
   * EXERCISE 2
   * 
   * Try to make a copy of an existing `Email` using `Email#copy` and note what happens.
   * 
   */
  //def changeEmail(email: Email): Email = ???

  // // This doesn't work. But is important because it would otherwise pass our validation...
  //def changeEmail(email: Email): Email = email.copy(email = "not a real email!")

  // // We could do this in Scala 2, but now we can't (because of the apply):
  // new Email("foo")

  // // The workaround was to define our own apply and make it private...

  /*
  Georgi KrastevToday at 2:51 PM
  You have to use `sealed abstract case class` in Scala 2
  */

  // But now in Scala 3, how do we do this?


  /**
   * EXERCISE 3
   * 
   * Try to create an Email directly by using the generated constructor in the companion object.
   * 
   */
  def caseClassApply(value: String): Email = ???

/**
 * PATTERN MATCHING
 * 
 * Scala 3 provides upgrades to the power and flexibility of pattern matching.
 */  
object pattern_matching:
  /**
   */
  def foo: Int = 2
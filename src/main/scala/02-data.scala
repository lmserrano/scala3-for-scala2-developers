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
  // sealed trait DayOfWeek
  // object DayOfWeek:
  //   case object Sunday extends DayOfWeek
  //   case object Monday extends DayOfWeek
  //   case object Tuesday extends DayOfWeek
  //   case object Wednesday extends DayOfWeek
  //   case object Thursday extends DayOfWeek
  //   case object Friday extends DayOfWeek
  //   case object Saturday extends DayOfWeek
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
  def daysOfWeek: Array[DayOfWeek] = ???
  def sunday: DayOfWeek = ???

  /**
   * EXERCISE 3
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type of any of the case constructors!
   */
  sealed trait Color 
  object Color:
    case object Red extends Color 
    case object Green extends Color 
    case object Blue extends Color
    final case class Custom(red: Int, green: Int, blue: Int) extends Color

  /**
   * EXERCISE 4
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   */
  sealed trait Result[+Error, +Value]
  object Result:
    final case class Succeed[Value](value: Value) extends Result[Nothing, Value]
    final case class Fail[Error](error: Error) extends Result[Error, Nothing]

  /**
   * EXERCISE 5
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   */
  sealed trait Workflow[-Input, +Output]
  object Workflow:
    final case class End[Output](value: Output) extends Workflow[Any, Output]

  /**
   * EXERCISE 6
   * 
   * Convert this "sealed trait" to an enum.
   */
  sealed trait Conversion[-From, +To]
  object Conversion:
    case object AnyToString extends Conversion[Any, String]
    case object StringToInt extends Conversion[String, Option[Int]]

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
  final case class Email(value: String)
  object Email:
    def fromString(v: String): Option[Email] = ???

    def isValidEmail(v: String): Boolean = v.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")

  /**
   * EXERCISE 2
   * 
   * Try to make a copy of an existing `Email` using `Email#copy` and note what happens.
   * 
   */
  def changeEmail(email: Email): Email = ???

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
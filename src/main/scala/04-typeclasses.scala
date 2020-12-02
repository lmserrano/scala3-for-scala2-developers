/**
 * TYPECLASSES
 * 
 * Scala 3 introduces direct support for typeclasses using contextual features of the language.
 * Typeclasses provide a way to abstract over similar data types, without having to change the 
 * inheritance hierarchy of those data types, providing the power of "mixin" interfaces, but 
 * with additional flexibility that plays well with third-party data types.
 */
object type_classes:

  object scope:
    //final case class Person(name: String, age: Int)

    // // Too concrete...
    //final case class PersonEndpoint()
    // def publish(personEndpoint: PersonEndpoint) = ???

    // // Now it forgot too much... there are no similarities anymore... we don't know the ways in which they are similar...
    //final case class Endpoint[A]()
    //def publish[A](endpoint: Endpoint[A]) = ???

    // // Solution 2:
    // // We can solve the problem like this, but it has drawbacks: fromJson ...
    // trait Json
    // trait JsonSerializable {
    //   def serialize(): Json
    //   def deserialize(json: Json): Unit // 2 problems: Will ruin mutability. Our case classes become useless; And a 2nd problem: To deserialize something of a type we need something of that type.
    // }

    // final case class Person(name: String, age: Int) extends Person {
    //   def serialize(): Json = ???

    //   def deserialize(json: Json): Unit = ??? // Problematic...
    // }

    // final case class Endpoint[A]()
    // def publish[A <: JsonSerializable](endpoint: Endpoint[A]) = ???

    // // Solution 3:
    // // We cannot publish for data types we don't control, because we can't make them extend JsonSerializable...
    // // This isn't a too good solution...

    // // Talk about typeclasses. `Comparable` is an example of one of those, in Java.

    // // But this is a pain as A gets more complex...
    // final case class Person(name: String, age: Int)

    // final case class Endpoint[A]()

    // trait Json
    // trait JsonCodec[A]:
    //   def serialize(a: A): Json
    //   def deserialize(json: Json): A

    // val PersonJsonSerializable: JsonCodec[Person] = ??? // in Scala 2 would have been an implicit probably

    // // // Would be really nice to call it like this
    // // ( ??? : Person).serialize

    // def publish[A](endpoint: Endpoint[A], json: JsonCodec[A]) = ???

    // // Scala 2 doesn't have first class support for Type Classes, but people would use implicits to, among many other things, emulate this, enabling people to use more type classes.

    // In Scala 3, we change this:
    final case class Person(name: String, age: Int)

    final case class Endpoint[A]()

    trait Json
    trait JsonCodec[A]:
      // We turn this (or these if we want this for deserialize too) into extension methods
      extension (a: A) def serialize: Json
      def deserialize(json: Json): A

    // We are defining na instance of JsonCodec for my "Person" data type.
    given JsonCodec[Person]:
      extension (a: Person) def serialize(a: Person): Json = ???
      def deserialize(json: Json): Person = ???

    // We say we will be using these JsonCodec capabilities for type A
    def publish[A](endpoint: Endpoint[A])(using json: JsonCodec[A]) =
      ???

    // The difference between implicit def and implicit val?
    // We are operating at an higher level with "given"...

    // Implicit val defines a base case
    // Implicit def defines an inductive case

    // Equivalent of an implicit def, in Scala 3
    given [A](using a: JsonCodec[A]) as JsonCodec[List[A]]:
      extension (list: List[A]) def serialize: Json = ???
      def deserialize(json: Json): List[A] = ???


  // ----

  trait PrettyPrint[-A]:
    extension (a: A) def prettyPrint: String

  given PrettyPrint[String]:
    extension (a: String) def prettyPrint: String = a

  "foo".prettyPrint

  final case class Person(name: String, age: Int)

  /**
   * EXERCISE 1
   * 
   * With the help of the `given` keyword, create an instance of the `PrettyPrint` typeclass for the 
   * data type `Person` that renders the person in a pretty way.
   */
  // given

  /**
   * EXERCISE 2
   * 
   * With the help of the `given` keyword, create a **named* instance of the `PrettyPrint` typeclass 
   * for the data type `Int` that renders the integer in a pretty way.
   */
  // given intPrettyPrint as ...

  /**
   * EXERCISE 3
   * 
   * Using the `summon` function, summon an instance of `PrettyPrint` for `String`.
   */
  val stringPrettyPrint: PrettyPrint[String] = ???

  /**
   * EXERCISE 4
   * 
   * Using the `summon` function, summon an instance of `PrettyPrint` for `Int`.
   */
  val intPrettyPrint: PrettyPrint[Int] = ???

  /**
   * EXERCISE 5
   * 
   * With the help of the `using` keyword, create a method called `prettyPrintIt` that, for any type 
   * `A` for which a `PrettyPrint` instance exists, can both generate a pretty-print string, and 
   * print it out to the console using `println`.
   */
  def prettyPrintIt = ???

  /**
   * EXERCISE 6
   * 
   * With the help of both `given` and `using`, create an instance of the `PrettyPrint` type class
   * for a generic `List[A]`, given an instance of `PrettyPrint` for the type `A`.
   */
  given [A] as PrettyPrint[List[A]]:
    extension (a: List[A]) def prettyPrint: String = ???

  /**
   * EXERCISE 7
   * 
   * With the help of both `given` and `using`, create a **named** instance of the `PrettyPrint` 
   * type class for a generic `Vector[A]`, given an instance of `PrettyPrint` for the type `A`.
   */
  // given vectorPrettyPrint[A] as ...

  import scala.Eql._ 

  /**
   * EXERCISE 8
   * 
   * Using the `derives` clause, derive an instance of the type class `Eql` for 
   * `Color`.
   */
  enum Color:
    case Red 
    case Green 
    case Blue

/**
 * IMPLICIT CONVERSIONS
 * 
 * Scala 3 introduces a new type class called `Conversion` to perform "implicit 
 * conversions"--the act of automatically converting one type to another.
 */
object conversions:
  final case class Rational(n: Int, d: Int)

  /**
   * EXERCISE 1
   * 
   * Create an instance of the type class `Conversion` for the combination of types
   * `Rational` (from) and `Double` (to).
   */
  // given ...
  given Conversion[Rational, Double] = ???

  /**
   * EXERCISE 2
   * 
   * Multiply a rational number by 2.0 (a double) to verify your automatic
   * conversion works as intended.
   */
  Rational(1, 2)
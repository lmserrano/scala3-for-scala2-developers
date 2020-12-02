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
      extension (a: Person) def serialize: Json = ???
      def deserialize(json: Json): Person = ???

    // // We say we will be using these JsonCodec capabilities for type A
    //def publish[A](endpoint: Endpoint[A])(using json: JsonCodec[A]) =
    //  ???
    // // OR
    // def publish[A: JsonCodec](endpoint: Endpoint[A]) = ???


    // The difference between implicit def and implicit val?
    // We are operating at an higher level with "given"...

    // Implicit val defines a base case
    // Implicit def defines an inductive case

    // Equivalent of an implicit def, in Scala 3
    given [A](using a: JsonCodec[A]) as JsonCodec[List[A]]:
      extension (list: List[A]) def serialize: Json = ???
      def deserialize(json: Json): List[A] = ???

    // // OR
    // object JsonCodec:
    //   given [A: JsonCodec] as JsonCodec[List[A]]:
    //     extension (list: List[A]) def serialize: Json = ???
    //     def deserialize(json: Json): List[A] = ???


    // we can use naming:
    // object Person:
      // given jsonCodecPerson as JsonCodec[Person]:
      //   extension (a: Person) def serialize: Json = ???
      //   def deserialize(json: Json): Json = ???


    // // There was another way to do this before, in the video. Re-watch.
    def publish[A: JsonCodec](endpoint: Endpoint[A]) =  // or Endpoint[A))(using jsonCodec: JsonCodec[A]) = ... // If we set this, when we call publish with `publish(???: Endpoint[List[Person]])` , we don't have to specify a parameter because Scala will use the "using" to find the codec (or may actually have to build that instance and construct it if necessary)
      val jsonCodec = summon[JsonCodec[A]] // "summon" allows to materialize the JsonCodec value for the provided type. Serves the same purposes as implicitly in Scala 2.x
      ???

  // ----

  // Review
  // Type Classes
  // Traits allow us to specify how things can be similar. JsonCodec example. The way in which a Person can be the same as a Catalogue, for example. We are abstracting over the similar ways in which we can serialize
  // How do we abstract? We use a typeclass (to avoid the other drawbacks we saw of using an interface)

  // 1. We create a type class that talks about how these different data types are the same
  // 2. Then we create instances or implementations of this trait, for different data types in our application, with the `given` keyword, `as` type JsonCodec[Person/Whatever]
  // 3. We can put that into the companion object of the data type. To make sure Scala can find it when it's looking for it in a context-bound usage with publish and summon
  // 4. In more complex scenarios, with Lists and other polymorphic types, we can define a polymorphic inductive instance vs the monomorphic jsoncodec person we saw before, which we named with `as JsonCodec[Person]`

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
package codetest
import com.example.codetest.Headline
import org.scalacheck.Gen

object generators {
  val headlineGen: Gen[Headline] =
    for {
      name     <- Gen.alphaStr
      password <- Gen.alphaStr
    } yield Headline(name, password)
}

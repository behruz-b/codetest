package codetest
import com.example.codetest.Headline
import java.time.LocalDateTime
import org.scalacheck.Gen

object generators {
  val headlineGen: Gen[Headline] =
    for {
      title <- Gen.alphaStr
      link  <- Gen.alphaStr
    } yield Headline(title, link, LocalDateTime.now())
}

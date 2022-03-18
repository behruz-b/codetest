package codetest

import com.example.codetest.Headline
import derevo.cats.show
import derevo.circe.magnolia.encoder
import derevo.derive

package object models {
  @derive(encoder, show)
  final case class Data(news: List[Headline])

  @derive(encoder, show)
  final case class NewsResponse(data: Data)
}

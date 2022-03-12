package com.example.codetest.test.api
import com.example.codetest.Headline

final case class Data(news: List[Headline])
final case class NewsResponse(data: Data)

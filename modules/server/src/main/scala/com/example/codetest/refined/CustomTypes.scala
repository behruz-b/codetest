package com.example.codetest.refined
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

object CustomTypes {

  type URL = String Refined Url

}

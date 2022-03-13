package com.example.codetest
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.Url

object RefinedCustomTypes {

  type URL = String Refined Url

}

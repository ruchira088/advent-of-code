package com.ruchij.twentytwentyone

object Utils {
  object IntValue {
    def unapply(input: String): Option[Int] = input.toIntOption
  }

}

package com.ruchij

trait Solution[+A] {
  def solve(input: List[String]): A
}
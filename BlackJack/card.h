#pragma once

#include <map>

enum class Suit {
  DIAMONDS,
  CLUBS,
  HEARTS,
  SPADES,
};

enum class Card : int {
  ONE,
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX ,
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING,
  ACE,
  NO_CARD = 0,
};

const std::map<Suit, std::string> Suits = {{Suit::DIAMONDS, "diamonds"},
                                           {Suit::CLUBS, "clubs"},
                                           {Suit::HEARTS, "hearts"},
                                           {Suit::SPADES, "spades"}};
const std::map<Card, std::string> Cards = {
    {Card::ONE, "1"},     {Card::TWO, "2"},     {Card::THREE, "3"},
    {Card::FOUR, "4"},    {Card::FIVE, "5"},    {Card::SIX, "6"},
    {Card::SEVEN, "7"},   {Card::EIGHT, "8"},   {Card::NINE, "9"},
    {Card::TEN, "10"},    {Card::JACK, "jack"}, {Card::QUEEN, "queen"},
    {Card::KING, "king"}, {Card::ACE, "ace"}};

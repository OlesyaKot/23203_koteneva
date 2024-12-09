#pragma once

#include <string>
#include <vector>

#include "card.h"

class Deck {
 public:
  virtual const std::pair<Card, Suit> get_card() = 0;
};

class Simple_deck : public Deck {
 public:
  const std::pair<Card, Suit> get_card() override;

 private:
  const int FACT_CARD = 11;
};

class N_deck : public Deck {
 public:
  N_deck(std::string m);
  const std::pair<Card, Suit> get_card() override;

 private:
  std::vector<std::pair<Card, Suit>> deck;
  int NUM_DECK = 0;
};

// если пустая колода - написать в первой строке assert(!deck.empty());   // #include <assert.h> 
// vector.emplace_back()  // в аргементе конструируется об.ект и сразу добавляет его в вектор  - так избегаем лишнего копирования

// ПОДУМАТЬ:
//как зарегестрировать несколько колод (имя ???)
#pragma once

#include <algorithm>
#include <string>
#include <vector>

#include "card.h"

class Deck {
 public:
  virtual const std::pair<Card, Suit> get_card() = 0;

  virtual void reset() = 0;

  virtual ~Deck() = default;
};

class Simple_deck : public Deck {
 public:
  const std::pair<Card, Suit> get_card() override;

  void reset() override {};

 private:
  static constexpr int FACT_CARD = 11;
};

class N_deck : public Deck {
 public:
  N_deck(int num_decks);
  const std::pair<Card, Suit> get_card() override;

  void reset() override;

 private:
  void fill_deck(){
    for (int i = 0; i < num; ++i) {
      for (auto suit : Suits) {
        for (auto card : Cards) {
          if (suit.second == "1") continue;
          deck.push_back(std::make_pair(card.first, suit.first));
        }
      }
    }
    std::random_shuffle(deck.begin(), deck.end());
  }

  int num = 0;
  std::vector<std::pair<Card, Suit>> deck;
};
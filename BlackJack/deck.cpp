#include "deck.h"

#include <algorithm>
#include <string>

#include "factory.h"

const std::pair<Card, Suit> Simple_deck::get_card() {
  while (true) {
    int c = rand() % FACT_CARD;
    if (c == 0) continue;
    return std::make_pair(static_cast<Card>(c), Suit::DIAMONDS);
  }
}

N_deck::N_deck(std::string m){
  NUM_DECK = std::stoi(m);
  for (int i = 0; i < NUM_DECK; ++i) {
    for (const auto& suit : Suits) {
      for (const auto& card : Cards) {
        if (suit.second != "1")
          deck.push_back(std::make_pair(card.first, suit.first));
      }
    }
  }
  std::random_shuffle(deck.begin(), deck.end());
}

const std::pair<Card, Suit> N_deck::get_card() {
  auto card = deck.back();
  deck.pop_back();
  return card;
}

namespace {
  Creator<Deck, Simple_deck> a{"simple", []() -> auto { return new Simple_deck(); }};
  Creator<Deck, N_deck> b{"2", []() -> auto { return new N_deck("2"); }};
}
#include "deck.h"

#include <algorithm>
#include <stdexcept>
#include <string>

#include "factory.h"

const std::pair<Card, Suit> Simple_deck::get_card() {
  while (true) {
    int c = rand() % FACT_CARD;
    if (c == 0) continue;
    return std::make_pair(static_cast<Card>(c), Suit::DIAMONDS);
  }
}

N_deck::N_deck(int num_decks){
  if (num_decks <= 0) throw std::invalid_argument("Number of decks must be more than zero");
  for (int i = 0; i < num_decks; ++i) {
    for (const auto& suit : Suits) {
      for (const auto& card : Cards) {
        if (suit.second == "1") continue;
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
  Creator<Deck, Simple_deck, int> a{"simple", [](int N) -> auto { return new Simple_deck(); }};
  Creator<Deck, N_deck, int> b{"multiple"};
  Creator<Deck, N_deck, int> m{"my_deck"};
}
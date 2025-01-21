#include "deck.h"

#include <cassert>
#include <stdexcept>

#include "factory.h"

const std::pair<Card, Suit> Simple_deck::get_card() {
  while (true) {
    int c = rand() % FACT_CARD;
    if (c == 0) continue;
    return std::make_pair(static_cast<Card>(c), Suit::DIAMONDS);
  }
}

N_deck::N_deck(int num_decks) : num(num_decks){
  fill_deck();
}

void N_deck::reset() { 
  deck.clear();
  fill_deck();
}

const std::pair<Card, Suit> N_deck::get_card() {
  auto card = deck.back();
  assert(!deck.empty());
  deck.pop_back();
  return card;
}

namespace {
  Creator<Deck, Simple_deck, int> a{"simple", []([[maybe_unused]] int N) -> auto { return new Simple_deck(); }};
  Creator<Deck, N_deck, int> b{"multiple"};
}
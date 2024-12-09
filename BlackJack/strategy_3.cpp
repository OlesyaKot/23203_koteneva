#include "factory.h"
#include "strategy_3.h"
// если у противника номинал первой карты >= 9, то берем всего 4 карты, иначе 5

void Strategy_3::see_opposite_card(Card c) {
  opposite_card = c;
}

bool Strategy_3::next_move([[maybe_unused]] int card) {
  num_card++;
  opposite_card >= (Card)9 ? move = num_card < 4 : move = num_card < 5;

  return move;
}

const std::string Strategy_3::get_name() { return "strategy 3"; }

const std::string Strategy_3::get_move() {
  if (move) return "hit";
  return "stand";
}

void Strategy_3::consider_ace() {};

namespace {
Creator<Strategy, Strategy_3> c("strategy_3");
}

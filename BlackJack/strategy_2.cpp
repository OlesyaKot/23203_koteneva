#include "strategy_2.h"
#include "factory.h"
// берет всегда 3 карты

void Strategy_2::see_opposite_card([[maybe_unused]] Card c) {}

void Strategy_2::consider_ace() {};

bool Strategy_2::next_move([[maybe_unused]] int card) {
  num_card++;
  move = num_card < 3;
  return move;
}

const std::string Strategy_2::get_name() { return "strategy 2"; }

const std::string Strategy_2::get_move() {
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_2> c("strategy_2");
}

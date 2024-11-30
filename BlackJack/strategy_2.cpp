#include "strategy_2.h"
#include "factory.h"
// берет всегда 3 карты
#include <functional>


void Strategy_2::see_opposite_card(int card) {}

int Strategy_2::next_move(int card) {
  total_sum += card;
  num_card++;
  move = num_card < 3;
  return move;
}

std::string Strategy_2::get_name() { return "strategy 2"; }

std::string Strategy_2::get_move() {
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_2> c("strategy_2");
}

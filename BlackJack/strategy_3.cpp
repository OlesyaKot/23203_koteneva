#include "factory.h"
#include "strategy_3.h"
// если у противника номинал первой карты >= 9, то берем всего 4 карты, иначе 5
#include <functional>

void Strategy_3::see_opposite_card(int card) {
  opposite_card = card; 
}

int Strategy_3::next_move(int card) {
  total_sum += card;
  num_card++;

  opposite_card >= 9 ? move = num_card < 4 : move = num_card < 5;

  return move;
}

std::string Strategy_3::get_name() { return "strategy 3"; }

std::string Strategy_3::get_move() {
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_3> c("strategy_3");
}

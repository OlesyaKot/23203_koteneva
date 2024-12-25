#include "strategy_3.h"

#include "factory.h"
// если у противника нdминал первой карты >= 9, то берем 4 карты, иначе 5

Move Strategy_3::next_move([[maybe_unused]] int card){
  num_card++;
  opposite_card >= MAX_OP_CARD ? move = num_card < NUM_OF_STEPS
                               : move = num_card < NUM_OF_STEPS + 1;
  if (move) return Move::HIT;
  return Move::STAND;
}

void Strategy_3::reset() {
  num_card = 0;
  move = 0;
  opposite_card = 0;
}

namespace {
Creator<Strategy, Strategy_3> c("strategy_3");
}

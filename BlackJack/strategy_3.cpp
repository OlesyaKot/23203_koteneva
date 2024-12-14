#include "strategy_3.h"

#include "factory.h"
// если у противника номинал первой карты >= 9, то берем всего 4 карты, иначе 5


bool Strategy_3::next_move([[maybe_unused]] int card){
  num_card++;
  opposite_card >= MAX_OP_CARD ? move = num_card < NUM_OF_STEPS
                               : move = num_card < NUM_OF_STEPS + 1;
  return move;
}

const std::string Strategy_3::get_move(){
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_3> c("strategy_3");
}

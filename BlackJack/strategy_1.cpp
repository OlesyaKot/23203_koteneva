#include "strategy_1.h"

#include "factory.h"
// если в сумме > 17 очков, то больше не брать карту

bool Strategy_1::next_move(int card){
  total_sum += card;
  move = total_sum < 17;
  return move;
}

const std::string Strategy_1::get_move(){
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_1> c("strategy_1");
}

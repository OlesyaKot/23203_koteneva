#include "strategy_1.h"

#include "factory.h"
// если в сумме > 17 очков, то больше не брать карту

Move Strategy_1::next_move(int card){
  total_sum += card;
  move = total_sum < MAX_NUM;

  if (move) return Move::HIT;
  return Move::STAND;
}

void Strategy_1::reset(){
  total_sum = 0;
  move = 0;
}

namespace {
Creator<Strategy, Strategy_1> c("strategy_1");
}

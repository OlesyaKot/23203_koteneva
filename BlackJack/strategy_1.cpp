#include "strategy_1.h"
#include "factory.h"

#include <functional>
// если в сумме > 17 очков, то больше не брать карту

void Strategy_1::see_opposite_card(int card) {}

int Strategy_1::next_move(int card) {
  total_sum += card;
  move = total_sum < 17;
  return move;
}

std::string Strategy_1::get_name() { return "strategy 1"; }

std::string Strategy_1::get_move() {
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_1> c("strategy_1");
}

// namespace {
// bool b = Factory<std::string, Strategy>::get_instance()->
//           register_creator("strategy_1", create_strategy_1);
// }

//  
// enum (1, 2, ... , 10, валет, дама, король, туз) 
// шаблонный класс, в отдельный хедер. Он один для всех объектов
//  template <class Type>
//  struct Creator(
//    Creator(){
//      Factory<std::string, Strategy? Strategy * (*)()>:: get_instance()->register_creator("name", []() -> auto {return new Type})
//    }
//  )
//
//
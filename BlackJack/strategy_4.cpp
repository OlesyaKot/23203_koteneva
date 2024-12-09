#include "strategy_4.h"

#include <fstream>
#include <string>

#include "factory.h"

// чем больше итсумма тем больше карт с большим номиналом осталось в колоде
// если итсумма >= -2 берем карты, иначе нет
//  ИТСУММА:
//   1. от 2 до 6: +1  
//   2. от 7 до 9: 0
//   3. от 10 до туза: -1

void Strategy_4::see_opposite_card(Card c) { 
  opposite_card = c;
  update_chance_to_win((int)opposite_card);
  get_data_from_config();
}

bool Strategy_4::next_move(int card) {
  update_chance_to_win(card);
  move = chance_to_win > EXTREAM_VALUE;
  return move;
}

void Strategy_4::update_chance_to_win(int card){
  if ((int)card >= LOWER_LIMIT && (int)card <= AVERAGE_LIMIT) chance_to_win++;
  if ((int)card >= UPPER_LIMIT) chance_to_win--;
}

void Strategy_4::get_data_from_config() {
  std::string input_str;
  std::ifstream in;
  in.open("../strategy_4.txt");
  if (!in.is_open()) {
    return;
  }
  std::getline(in, input_str);
  s.str(input_str);

  LOWER_LIMIT = get_int_data();
  AVERAGE_LIMIT = get_int_data();
  UPPER_LIMIT = get_int_data();
  EXTREAM_VALUE = get_int_data();
  DELETE_ACE = get_int_data();

  in.close();
}

int Strategy_4::get_int_data(){
  std::getline(s, tmp, ' ');
  return std::stoi(tmp);
}

const std::string Strategy_4::get_name() {
  return "strategy 4";
}

const std::string Strategy_4::get_move() {
  if (move) return "hit";
  return "stand";
}

namespace {
Creator<Strategy, Strategy_4> c("strategy_4");
}

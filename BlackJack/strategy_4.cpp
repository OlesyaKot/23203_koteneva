#include "strategy.h"

#include <fstream>
#include <sstream>

#include "factory.h"

// чем больше итсумма тем больше карт с большим номиналом осталось в колоде
// если итсумма >= -2 берем карты, иначе нет
//  ИТСУММА:
//   1. номинал карты от 2 до 6: +1  
//   2. номинал карты от 7 до 9: 0
//   3. номинал карты от 10 до туза: -1

class Strategy_4 : public Strategy{
 public:
  void see_opposite_card(Card c) override {
    opposite_card = c;
    update_chance_to_win((int)opposite_card);
  }

  bool next_move(int card) override {  
    update_chance_to_win(card);
    move = chance_to_win > EXTREAM_VALUE;
    return move;
  }

  void update_chance_to_win(int card) {
    if (card >= LOWER_LIMIT && card <= AVERAGE_LIMIT) chance_to_win++;
    if (card >= UPPER_LIMIT) chance_to_win--;
  }

  void consider_ace() override {}

  void get_data_from_config(const std::string path) override {
    in.open(path + "strategy_4_config.txt");
    in.exceptions(std::ifstream::failbit);
    in.exceptions(std::ifstream::badbit);

    std::getline(in, input_str);
    s.str(input_str);
    LOWER_LIMIT = get_int_data();
    AVERAGE_LIMIT = get_int_data();
    UPPER_LIMIT = get_int_data();
    EXTREAM_VALUE = get_int_data();
    DELETE_ACE = get_int_data();
  
    in.close();
  }

  int get_int_data(){
    std::getline(s, tmp, ' ');
    return std::stoi(tmp);
  }

  const std::string get_name() override { return "strategy 4"; }

  const std::string get_move() override {
    if (move) return "hit";
    return "stand";
  }

 private:
  bool move = 0;
  Card opposite_card;
  int chance_to_win = 0;

  std::istringstream s;
  std::ifstream in;
  std::string input_str;
  std::string tmp;
  int LOWER_LIMIT = 0;
  int UPPER_LIMIT = 0;
  int AVERAGE_LIMIT = 0;
  int EXTREAM_VALUE = 0;
  int DELETE_ACE = 0;
};

namespace {
Creator<Strategy, Strategy_4> c("strategy_4");
}

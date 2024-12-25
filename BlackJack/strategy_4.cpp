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
  void see_opposite_card(int c) override {
    opposite_card = c;
    update_chance_to_win(opposite_card);
  }

  Move next_move(int card) override {  
    update_chance_to_win(card);
    move = chance_to_win > EXTREAM_VALUE;

    if (move) return Move::HIT;
    return Move::STAND;
  }

  void update_chance_to_win(int card) {
    if (card >= LOWER_LIMIT && card <= AVERAGE_LIMIT) chance_to_win++;
    if (card >= UPPER_LIMIT) chance_to_win--;
  }

  void consider_ace() override {}

  void reset() override {
    move = 0;
    opposite_card = 0;
    chance_to_win = 0;
  }

  void get_data_from_config(const std::string &path) override {
    std::ifstream in;
    in.open(path + "strategy_4_config.txt");
    in.exceptions(std::ifstream::failbit);
    in.exceptions(std::ifstream::badbit);

    std::string input_str;
    std::getline(in, input_str);
    s.str(input_str);
    LOWER_LIMIT = get_int_data();
    AVERAGE_LIMIT = get_int_data();
    UPPER_LIMIT = get_int_data();
    EXTREAM_VALUE = get_int_data();
    DELETE_ACE = get_int_data();
  }

  int get_int_data(){
    std::string tmp;
    std::getline(s, tmp, ' ');
    return std::stoi(tmp);
  }

  std::string get_name() override { return "strategy 4"; }

 private:
  bool move = 0;
  int opposite_card = 0;
  int chance_to_win = 0;

  std::istringstream s;
  int LOWER_LIMIT = 0;
  int UPPER_LIMIT = 0;
  int AVERAGE_LIMIT = 0;
  int EXTREAM_VALUE = 0;
  int DELETE_ACE = 0;
};

namespace {
Creator<Strategy, Strategy_4> c("strategy_4");
}

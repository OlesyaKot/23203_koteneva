#include "strategy.h"

#include <fstream>
#include <sstream>
#include <string>

#include "factory.h"
// если карта противника >= 9, то берем карты, пока сумма картa противника и наших не станет >= 30; 
// если карта противника от 6 до 8, то берем кары пока сумма не станет >= 25;
// если от 2 до 5, то пока не станет >= 20;

class Strategy_5 : public Strategy {
 public:
  void see_opposite_card(Card c) override {
    opposite_card = (int)c;
    t_sum_with_op_card += opposite_card;
  }

  bool next_move(int card) override {
    t_sum_with_op_card += card;
    if (opposite_card >= MAX_OP_CARD) move = t_sum_with_op_card < UPPER_LIMIT;
    if (opposite_card > MIN_OP_CARD && opposite_card < MAX_OP_CARD) move = t_sum_with_op_card < AVERAGE_LIMIT;
    if (opposite_card <= MIN_OP_CARD) move = t_sum_with_op_card < LOWER_LIMIT;
    return move;
  }

  void get_data_from_config(const std::string path) override {
    in.open(path + "strategy_5_config.txt");
    in.exceptions(std::ifstream::failbit);
    in.exceptions(std::ifstream::badbit);
    std::getline(in, input_str);
    s.str(input_str);

    MIN_OP_CARD = get_int_data();
    MAX_OP_CARD = get_int_data();
    LOWER_LIMIT = get_int_data();
    AVERAGE_LIMIT = get_int_data();
    UPPER_LIMIT = get_int_data();
    DELETE_ACE = get_int_data();
  
    in.close();
  }

  int get_int_data() {
    std::getline(s, tmp, ' ');
    return std::stoi(tmp);
  }

  void consider_ace() override { t_sum_with_op_card -= DELETE_ACE; }

  const std::string get_name() override { return "strategy 5"; }

  const std::string get_move() override {
    if (move) return "hit";
    return "stand";
  }

 private:
  int t_sum_with_op_card = 0;
  bool move = 0;
  int opposite_card = 0;

  std::istringstream s;
  std::ifstream in;
  std::string input_str;
  std::string tmp;

  int MIN_OP_CARD = 0;
  int MAX_OP_CARD = 0;
  int LOWER_LIMIT = 0;
  int AVERAGE_LIMIT = 0;
  int UPPER_LIMIT = 0;
  int DELETE_ACE = 0;
};

namespace {
Creator<Strategy, Strategy_5> c("strategy_5");
}

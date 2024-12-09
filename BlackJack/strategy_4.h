#pragma once

#include <sstream>
#include <vector>

#include "strategy.h"

class Strategy_4:public Strategy {
 public:
  void see_opposite_card(Card c) override;
  bool next_move(int card) override;
  const std::string get_name() override;
  const std::string get_move() override;
  void consider_ace() override { total_sum -= DELETE_ACE; }

 private:
  void update_chance_to_win(int card);
  void get_data_from_config();
  int get_int_data();
  int total_sum = 0;
  int num_card = 0;
  bool move = 0;
  Card opposite_card;
  int chance_to_win = 0;

  std::istringstream s;
  std::string tmp;
  int LOWER_LIMIT = 0;
  int UPPER_LIMIT = 0;
  int AVERAGE_LIMIT = 0;
  int EXTREAM_VALUE = 0;
  int DELETE_ACE = 0;
};

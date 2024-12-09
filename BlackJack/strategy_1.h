#pragma once

#include <vector>
#include "strategy.h"

class Strategy_1 : public Strategy {
 public:
  void see_opposite_card(Card c) override;
  bool next_move(int card) override;
  const std::string get_name() override;
  const std::string get_move() override;
  void consider_ace() override { total_sum -= DELETE_ACE; }

 private:
  int total_sum = 0;
  bool move = 0;
  const int DELETE_ACE = 10;
};

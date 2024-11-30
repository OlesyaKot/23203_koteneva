#pragma once

#include "strategy.h"
#include <vector>

class Strategy_2 : public Strategy {
 public:
  void see_opposite_card(int card) override;
  int next_move(int card) override;
  std::string get_name() override;
  std::string get_move() override;

 private:
  int total_sum = 0;
  int num_card = 0;
  bool move = 0;
};

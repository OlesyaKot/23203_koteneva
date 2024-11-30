#pragma once

#include <vector>

#include "strategy.h"

class Strategy_3:public Strategy {
 public:
  void see_opposite_card(int card) override;
  int next_move(int card) override;
  std::string get_name() override;
  std::string get_move() override;

 private:
  int total_sum = 0;
  int num_card = 0;
  bool move = 0;
  int opposite_card = 0;
};
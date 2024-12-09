#pragma once

#include <vector>

#include "strategy.h"

class Strategy_3:public Strategy {
 public:
  void see_opposite_card(Card c) override;
  bool next_move(int card) override;
  const std::string get_name() override;
  const std::string get_move() override;
  void consider_ace() override;

 private:
  int num_card = 0;
  bool move = 0;
  Card opposite_card;
};
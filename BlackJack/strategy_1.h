#pragma once

#include "strategy.h"
// если в сумме > 17 очков, то больше не брать карту

class Strategy_1 : public Strategy {
 public:
  void see_opposite_card([[maybe_unused]] Card c) override {}

  bool next_move(int card) override;

  const std::string get_name() override { return "strategy 1"; }

  const std::string get_move() override;

  void consider_ace() override { total_sum -= DELETE_ACE; }

  void get_data_from_config([[maybe_unused]] const std::string path) override {}

 private: 
  int total_sum = 0;
  bool move = 0;
  const int DELETE_ACE = 10;
};

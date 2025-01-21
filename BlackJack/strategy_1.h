#pragma once

#include "strategy.h"

class Strategy_1 : public Strategy {
 public:
  void see_opposite_card([[maybe_unused]] int c) override {}

  Move next_move(int card) override;

  void reset() override;

  std::string get_name() override { return "strategy 1"; }

  void consider_ace() override { total_sum -= DELETE_ACE; }

  void get_data_from_config([[maybe_unused]] const std::string &path) override {}

 private: 
  int total_sum = 0;
  bool move = 0;
  static constexpr int DELETE_ACE = 10;
  static constexpr int MAX_NUM = 17;
};

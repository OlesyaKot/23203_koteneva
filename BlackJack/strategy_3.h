#pragma once

#include "strategy.h"

class Strategy_3 : public Strategy {
 public:
  virtual void see_opposite_card(int c) override { opposite_card = c; }

  Move next_move([[maybe_unused]] int card) override;

  void reset() override;

  std::string get_name() override { return "strategy 3"; }

  void consider_ace() override {}

  void get_data_from_config([[maybe_unused]] const std::string &path) override {}

 private:
  int num_card = 0;
  bool move = 0;
  int opposite_card = 0;
  static constexpr int MAX_OP_CARD = 9;
  static constexpr int NUM_OF_STEPS = 4;
};

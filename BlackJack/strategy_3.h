#pragma once

#include "strategy.h"
// если у противника номинал первой карты >= 9, то берем всего 4 карты, иначе 5

class Strategy_3 : public Strategy {
 public:
  virtual void see_opposite_card(Card c) override { opposite_card = (int)c; }

  bool next_move([[maybe_unused]] int card) override;

  const std::string get_name() override { return "strategy 3"; }

  const std::string get_move() override;

  void consider_ace() override {}

  void get_data_from_config([[maybe_unused]] const std::string path) override {}

 private:
  int num_card = 0;
  bool move = 0;
  int opposite_card = 0;
  int MAX_OP_CARD = 9;
  int NUM_OF_STEPS = 4;
};

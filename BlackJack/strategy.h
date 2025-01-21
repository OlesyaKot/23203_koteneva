#pragma once

#include <string>

#include "card.h"
#include "move.h"

class Strategy {
 public:
  virtual void see_opposite_card(int c) = 0;

  virtual Move next_move(int card) = 0;

  virtual void get_data_from_config(const std::string &path) = 0;

  virtual void reset() = 0;

  virtual void consider_ace() = 0;

  virtual std::string get_name() = 0;

  virtual ~Strategy() = default;
};

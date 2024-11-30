#pragma once

#include <string>

class Strategy {
 public:
  virtual void see_opposite_card(int card) = 0;
  virtual int next_move(int card) = 0;
  virtual std::string get_name() = 0;
  virtual std::string get_move() =  0;
};

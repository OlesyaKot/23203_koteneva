#pragma once

#include <string>
#include "card.h"

class Strategy {
 public:
  virtual void see_opposite_card(Card c) = 0;
  virtual bool next_move(int card) = 0;

  virtual void consider_ace() = 0;

  virtual const std::string get_name() = 0;
  virtual const std::string get_move() =  0;
};

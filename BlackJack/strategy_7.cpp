#include "strategy.h"
#include "strategy_1.h"
#include "strategy_3.h"

#include "factory.h"
// метастратегия, основывается на strategy 1 и strategy 3

class Strategy_7 : public Strategy{
 public:
  void see_opposite_card(int c) override { 
    s3.see_opposite_card(c);
  }

  Move next_move(int card) override { 

  if (s1.next_move(card) == Move::HIT && s3.next_move(card) == Move::HIT)
    return Move::HIT;
  return Move::STAND;
  }

  void reset() override {};

  void consider_ace() override { s1.consider_ace(); }

  std::string get_name() override { return "strategy 7"; }

  void get_data_from_config([[maybe_unused]] const std::string &path) override {}

 private:
  Strategy_1 s1;
  Strategy_3 s3;
};

namespace {
Creator<Strategy, Strategy_7> c("strategy_7");
}
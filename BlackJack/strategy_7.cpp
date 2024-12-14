#include "strategy.h"
#include "strategy_1.h"
#include "strategy_3.h"

#include "factory.h"
// метастратегия, основывается на strategy 1 и strategy 3

class Strategy_7 : public Strategy{
 public:
  void see_opposite_card(Card c) override { 
    s3.see_opposite_card(c);
  }

  bool next_move(int card) override { 
    move = s1.next_move(card) && s3.next_move(card);
    return move;
  }

  void consider_ace() override { s1.consider_ace(); }

  const std::string get_name() override { return "strategy 7"; }

  const std::string get_move() override {
    if (move) return "hit";
    return "stand";
  }

  void get_data_from_config([[maybe_unused]] const std::string path) override {}

 private:
  Strategy_1 s1;
  Strategy_3 s3;
  int move = 0;
};

namespace {
Creator<Strategy, Strategy_7> c("strategy_7");
}
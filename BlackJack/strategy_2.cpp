#include "strategy.h"

#include "factory.h"
// берет всегда 3 карты

class Strategy_2 : public Strategy{
 public:
  void see_opposite_card([[maybe_unused]] Card c) override {}

  void consider_ace() override {}

  void get_data_from_config([[maybe_unused]] const std::string path) override {}

  bool next_move([[maybe_unused]] int card) override {
    num_card++;
    move = num_card < 3;
    return move;
  }

  const std::string get_name() override { return "strategy 2"; }

  const std::string get_move() override {
    if (move) return "hit";
    return "stand";
  }

 private:
  int num_card = 0;
  bool move = 0;
};

namespace {
Creator<Strategy, Strategy_2> c("strategy_2");
}

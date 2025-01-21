#include "strategy.h"

#include "factory.h"
// берет всегда 3 карты

class Strategy_2 : public Strategy{
 public:
  void see_opposite_card([[maybe_unused]] int c) override {}

  void consider_ace() override {}

  void get_data_from_config([[maybe_unused]] const std::string &path) override {}

  Move next_move([[maybe_unused]] int card) override {
    num_card++;
    move = num_card < MAX_NUM_CARD;
    if (move) return Move::HIT;
    return Move::STAND;
  }

  void reset() override{
    num_card = 0;
    move = 0;
  }

  std::string get_name() override { return "strategy 2"; }

 private:
  int num_card = 0;
  bool move = 0;
  static constexpr int MAX_NUM_CARD = 3;
};

namespace {
Creator<Strategy, Strategy_2> c("strategy_2");
}

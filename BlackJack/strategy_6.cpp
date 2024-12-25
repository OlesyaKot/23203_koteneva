#include "strategy.h"

#include "factory.h"
// берем карты до тех пор, пока нам не придет карта, номинал которой равен первой карте противника

class Strategy_6 : public Strategy {
 public:
  void see_opposite_card(int c) override { opposite_card = c; }

  Move next_move(int card) override {
    move = card != opposite_card;

    if (move) return Move::HIT;
    return Move::STAND;
  }

  void reset() override {
    move = 0;
    opposite_card = 0;
  }

  std::string get_name() override { return "strategy 6"; }

  void consider_ace() override {};

  void get_data_from_config([[maybe_unused]] const std::string &path) override {}

 private:
  bool move = 0;
  int opposite_card = 0;
};

namespace {
Creator<Strategy, Strategy_6> c("strategy_6");
}

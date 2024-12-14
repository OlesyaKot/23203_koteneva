#include "strategy.h"

#include "factory.h"
// берем карты до тех пор, пока нам не придет карта, номинал которой равен первой карте противника

class Strategy_6 : public Strategy {
 public:
  void see_opposite_card(Card c) override { opposite_card = c; }

  bool next_move(int card) override {
    move = (Card)card != opposite_card;

    return move;
  }

  const std::string get_name() override { return "strategy 6"; }

  const std::string get_move() override {
    if (move) return "hit";
    return "stand";
  }

  void consider_ace() override {};

  void get_data_from_config([[maybe_unused]] const std::string path) override {}

 private:
  bool move = 0;
  Card opposite_card;
};

namespace {
Creator<Strategy, Strategy_6> c("strategy_6");
}

#pragma once

#include <map>
#include <memory>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include "deck.h"
#include "strategy.h"

enum class Mode {
  DETAILED,
  FAST,
};

class Business_logic{
  public:
    virtual void game(std::vector<Strategy*> strategies, Deck* deckmode) = 0;

    virtual ~Business_logic() = default; 
};

class Competition : public Business_logic {
 public:
  Competition(Mode m): mode(m) {};

  void game(std::vector<Strategy*> strategies, Deck* deckmode) override;

  const std::string get_winner() { 
    return winner; 
  }

  bool check_total_sum();

 private:
  int get_int_card(Card c) {
    switch (c) {
      case Card::ONE : return 1;
      case Card::TWO : return 2;
      case Card::THREE : return 3;
      case Card::FOUR : return 4;
      case Card::FIVE : return 5;
      case Card::SIX : return 6;
      case Card::SEVEN : return 7;
      case Card::EIGHT : return 8;
      case Card::NINE : return 9;
      case Card::TEN : return 10;
      case Card::JACK : return 10;
      case Card::QUEEN : return 10;
      case Card::KING : return 10;
      case Card::ACE : return 11;
      default : return 0;
    };
  }

  Mode mode;
  Strategy* s1;
  Strategy* s2;
  std::unique_ptr<Deck> deck;

  int total_sum_1 = 0;
  int total_sum_2 = 0;
  Move flag1 = Move::HIT;
  Move flag2 = Move::HIT;
  int aces_s1 = 0;
  int aces_s2 = 0;
  std::string winner;
  };

class Tournament : public Business_logic {
 public:
  void game(std::vector<Strategy *> strategies, Deck* deckmode) override;
 private:
  std::map<std::string, int> game_score;
  std::vector<std::pair<std::string, int>> score;
};

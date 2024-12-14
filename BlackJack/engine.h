#pragma once

#include <map>
#include <memory>
#include <string>
#include <vector>

#include "deck.h"
#include "strategy.h"

enum class Mode {
  DETAILED,
  FAST,
};

class Business_logic{
  public:
    virtual void game(std::vector<std::string> strategies, std::string deckmode, std::string config, int N) = 0;
};

class Competition : public Business_logic {
 public:
  Competition(Mode m): mode(m) {};

  void game(std::vector<std::string> strategies, std::string deckmode, std::string config, int N) override;

  const std::string get_winner() { 
    return winner; 
    }

  bool check_total_sum();

 private:
  Mode mode;
  std::unique_ptr<Strategy> s1;
  std::unique_ptr<Strategy> s2;
  std::unique_ptr<Deck> deck;

  int total_sum_1 = 0;
  int total_sum_2 = 0;
  bool flag1 = 1;
  bool flag2 = 1;
  int aces_s1 = 0;
  int aces_s2 = 0;
  std::string winner;
  };

class Tournament : public Business_logic {
 public:
  void game(std::vector<std::string> strategies, std::string deckmode, std::string config, int N) override;
 private:
  std::map<std::string, int> game_score;
  std::vector<std::pair<std::string, int>> score;
};

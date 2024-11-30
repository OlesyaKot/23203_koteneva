#pragma once

#include <string>
#include <vector>

enum class Mode {
  DETAILED,
  FAST,
};

class Business_logic{
  public:
    virtual void game(std::vector<std::string> strategies) = 0;
};

class Competition : public Business_logic {
 public:
  Competition(Mode m): mode(m) {};

  void game(std::vector<std::string> strategies) override;

 private:
  Mode mode;
  std::unique_ptr<Strategy> s1;
  std::unique_ptr<Strategy> s2;
  std::vector<int> for_s1;
  std::vector<int> for_s2;
  int total_sum_1 = 0;
  int total_sum_2 = 0;
  bool flag1 = 1;
  bool flag2 = 1;
  std::string winner;
};

class Tournament : public Business_logic {
 public:
  void game(std::vector<std::string> strategies) override;
};

int get_card();

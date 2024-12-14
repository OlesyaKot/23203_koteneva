#pragma once

#include <iostream>
#include <string>

#include "card.h"

class User_interface {
 public:
  User_interface() = default;
  ~User_interface() = default;
  void print_curr_round();
  void print_res(const std::string name, const int summ);
  void print_res_draw(const int total_sum_1);
  void print_curr_move(const std::string name, const Card card, const Suit suit, const int total_summ, const std::string move);
  void print_pair(const std::string s1, const std::string s2);
  void print_winner(const std::string winner);
  void print_winner_string();
  bool is_finish();
  void print_error();
  void print_config_error();
  void print_invalid_arg_error(const std::string s);

 private:
  std::istream& s = std::cin;
  std::string bottom;
  int round = 0;
  const int MAX_SUM = 21;
};
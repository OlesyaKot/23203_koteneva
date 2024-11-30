#pragma once

#include <string>

class User_interface {
 public:
  User_interface() = default;
  ~User_interface() = default;
  void print_curr_round();
  void print_res(std::string name, const int summ);
  void print_curr_move(std::string name, const int card, const int total_summ, std::string move);
  void print_pair(std::string s1, std::string s2);
  void print_error();

 private:
  int round = 0;
};
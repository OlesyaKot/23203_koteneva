#pragma once

#include <iostream>
#include <string_view>

#include "card.h"
#include "move.h"

class User_interface {
 public:
  User_interface() = default;
  ~User_interface() = default;
  void print_curr_round();
  void print_res(std::string_view name, int summ);
  void print_res_draw(int total_sum_1);
  void print_curr_move(std::string_view name, Card card, Suit suit, int total_summ, Move move);
  void print_pair(std::string_view s1, std::string_view s2);
  void print_winner(std::string_view winner);
  void print_winner_string();
  bool is_finish();
  void print_error();
  void print_config_error();
  void print_invalid_arg_error(std::string_view s);
  void print_deck_error();

 private:

  std::string move_to_str(Move m){
    switch(m){
      case Move::HIT :
        return "hit";
      default :
        return "stand";
    }
  }

  std::istream& s = std::cin;
  std::string bottom;
  int round = 0;
  static constexpr int MAX_SUM = 21;
};
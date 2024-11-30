#include <iostream>

#include "user_interface.h"

void User_interface::print_res(std::string name, const int summ) {
  std::cout << name << " is win with total score " << summ << std::endl;
  std::cout << std::endl;
}

void User_interface::print_curr_round() {
  round++;
  std::cout << "Round " << round << std::endl;
}

void User_interface::print_curr_move(std::string name, const int card,
                                     const int total_summ, std::string move) {
  std::cout << name << ":" << std::endl;
  std::cout << "next card is " << card << std::endl;
  std::cout << "total sum is " << total_summ << std::endl;
  if (total_summ < 21) std::cout << "next move is " << move << std::endl;
  std::cout << std::endl;
}

void User_interface::print_error() { std::cout << "Error" << std::endl; }

void User_interface::print_pair(std::string s1, std::string s2) {
  std::cout << "Game between " << s1 << " and " << s2 << std::endl;
}

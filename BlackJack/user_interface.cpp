#include "user_interface.h"

#include <iostream>
#include <istream>

void User_interface::print_res(const std::string name, const int summ) {
  std::cout << name << " is win with total score " << summ << std::endl;
  std::cout << std::endl;
}

void User_interface::print_res_draw(const int total_sum_1){
  std::cout << "The result of the game is a draw with the total score "
            << total_sum_1 << " and " << total_sum_1 << std::endl;
  std::cout << std::endl;
}

void User_interface::print_curr_round() {
  round++;
  std::cout << "Round " << round << std::endl;
}

void User_interface::print_curr_move(const std::string name, const Card card, const Suit suit,
                                     const int total_summ, const std::string move) {
  std::cout << name << ":" << std::endl;
  std::cout << "next card is " << Cards.at(card) << " " << Suits.at(suit) << std::endl;
  std::cout << "total sum is " << total_summ << std::endl;
  if (total_summ < MAX_SUM) std::cout << "next move is " << move << std::endl;
  std::cout << std::endl;
}

void User_interface::print_winner_string(){
  std::cout << "The winner:" << std::endl;
}

void User_interface::print_winner(const std::string winner){
  std::cout << winner << std::endl;
}

void User_interface::print_pair(const std::string s1, const std::string s2) {
  std::cout << "Game between " << s1 << " and " << s2 << std::endl;
}

bool User_interface::is_finish(){
  std::cout << "Enter any symbol and press the bottom ENTER to countinue the game or just press ENTER." << std::endl;
  std::cout << "If you want to stop, enter quit. " << std::endl;

  std::getline(s, bottom);
  std::cout << std::endl;
  return "quit" == bottom || "q" == bottom || "Q" == bottom;
}

void User_interface::print_error() { 
  std::cout << "Error" << std::endl;
}

void User_interface::print_config_error() {
  std::cout << "Invalid configure path" << std::endl;
}

void User_interface::print_invalid_arg_error(const std::string s) {
  std::cout << s << std::endl;
  std::cout << std::endl;
}

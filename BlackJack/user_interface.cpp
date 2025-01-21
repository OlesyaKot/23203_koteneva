#include "user_interface.h"

#include <iostream>
#include <istream>
// заменить std::string на std::string_veiw
void User_interface::print_res(std::string_view name, int summ) {
  std::cout << name << " is win with total score " << summ << std::endl;
  std::cout << std::endl;
}

void User_interface::print_res_draw(int total_sum_1){
  std::cout << "The result of the game is a draw with the total score "
            << total_sum_1 << " and " << total_sum_1 << std::endl;
  std::cout << std::endl;
}

void User_interface::print_curr_round() {
  round++;
  std::cout << "Round " << round << std::endl;
}

void User_interface::print_curr_move(std::string_view name, Card card,
                                     Suit suit, int total_summ, Move move) {
  std::cout << name << ":" << std::endl;
  std::cout << "next card is " << Cards.at(card) << " " << Suits.at(suit) << std::endl;
  std::cout << "total sum is " << total_summ << std::endl;
  if (total_summ < MAX_SUM) std::cout << "next move is " << move_to_str(move) << std::endl;
  std::cout << std::endl;
}

void User_interface::print_winner_string(){
  std::cout << "The winner:" << std::endl;
}

void User_interface::print_winner(std::string_view winner) {
  std::cout << winner << std::endl;
}

void User_interface::print_pair(std::string_view s1, std::string_view s2) {
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

void User_interface::print_invalid_arg_error(std::string_view s) {
  std::cout << s << std::endl;
  std::cout << std::endl;
}

void User_interface::print_deck_error(){
  std::cout << "Number of decks must be more than zero" << std::endl;
}

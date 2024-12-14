#include "engine.h"

#include <algorithm>
#include <iostream>
#include <stdexcept>
#include <vector>

#include "factory.h"
#include "user_interface.h"

void Competition::game(std::vector<std::string> strategies, std::string deckmode, std::string config, int N) {
  User_interface user;
  
  try {
    s1 = Factory<std::string, Strategy, std::function<Strategy*()>>::get_instance()->create_by_name(strategies[0]);  
    s2 = Factory<std::string, Strategy, std::function<Strategy*()>>::get_instance()->create_by_name(strategies[1]);  
    deck = Factory<std::string, Deck, std::function<Deck*(int)>>::get_instance()->create_by_name(deckmode, N);
  } catch (const std::bad_alloc &a) {
    user.print_error();
    return;
  } catch (const std::invalid_argument &a){
    user.print_invalid_arg_error(a.what());
    return;
  }

  try{
    s1->get_data_from_config(config);
    s2->get_data_from_config(config);
  } catch (std::ios_base::failure& f){
    user.print_config_error();
    return;
  }

  auto card_s1 = deck->get_card();
  auto card_s2 = deck->get_card();

  s1->see_opposite_card(card_s2.first);
  s2->see_opposite_card(card_s1.first);

  while (1) {
    if (mode == Mode::DETAILED) 
      user.print_curr_round();

    card_s1.first == Card::ACE ? aces_s1++ : aces_s1;
    card_s2.first == Card::ACE ? aces_s2++ : aces_s2;

    if (flag1) {
      flag1 = s1->next_move((int)card_s1.first);
      total_sum_1 += (int)card_s1.first;

      if (mode == Mode::DETAILED){
        if (user.is_finish()) return;
        user.print_curr_move(s1->get_name(), card_s1.first, card_s1.second, total_sum_1, s1->get_move());
      }
    }

    if (check_total_sum()) return;

    if (flag2) {
      flag2 = s2->next_move((int)card_s2.first);
      total_sum_2 += (int)card_s2.first;

      if (mode == Mode::DETAILED){
        if (user.is_finish()) return;
        user.print_curr_move(s2->get_name(), card_s2.first, card_s2.second, total_sum_2, s2->get_move());
      }
    }

    if (check_total_sum()) return;

    card_s1 = deck->get_card();
    card_s2 = deck->get_card();
  }
};

bool Competition::check_total_sum() {
  User_interface user;

  if (total_sum_1 == 21) {
    if (mode == Mode::DETAILED && user.is_finish()) return 1;
    winner = s1->get_name();
    user.print_res(winner, total_sum_1);
    return 1;
  }

  if (total_sum_1 > 21) {
    if (mode == Mode::DETAILED && user.is_finish()) return 1;

    if (aces_s1){
      total_sum_1 -= 10;
      s1->consider_ace();
      return 0;
    }

    winner = s2->get_name();
    user.print_res(winner, total_sum_2);
    return 1;
  }

  if (total_sum_2 == 21) {
    if (mode == Mode::DETAILED && user.is_finish()) return 1;
    winner = s2->get_name();
    user.print_res(winner, total_sum_2);
    return 1;
  }

  if (total_sum_2 > 21) {
    if (mode == Mode::DETAILED && user.is_finish()) return 1;
    if (aces_s2) {
      total_sum_2 -= 10;
      s2->consider_ace();
      return 0;
    }

    winner = s1->get_name();
    user.print_res(winner, total_sum_1);
    return 1;
  }

  if (!flag1 && !flag2) {
    if (mode == Mode::DETAILED && user.is_finish()) return 1;
    if (total_sum_1 > total_sum_2) {
      winner = s1->get_name();
      user.print_res(winner, total_sum_1);
      return 1;
    }
    if (total_sum_2 > total_sum_1){
      winner = s2->get_name();
      user.print_res(winner, total_sum_2);
      return 1;
    }
    winner = {};  // случай если ничья, в турнире никому не прибавляется очки за победу 
    user.print_winner_string();
    user.print_res_draw(total_sum_1);
    return 1;
  }
  return 0;
}

void Tournament::game(std::vector<std::string> strategies, std::string deckmode, std::string config, int N) {
  User_interface user;

  for (size_t i = 0; i < strategies.size(); ++i) {
    for (size_t j = 0; j < strategies.size(); ++j) {
      if (i == j) continue;
      user.print_pair(strategies[i], strategies[j]);
      Competition c(Mode::FAST);
      std::vector<std::string> v;
      v.push_back(strategies[i]);
      v.push_back(strategies[j]);
      c.game(v, deckmode, config, N);
      if (c.get_winner() == "") continue;
      game_score[c.get_winner()]++;
    }
  }

  user.print_winner_string();

  std::transform(game_score.begin(), game_score.end(), std::back_inserter(score), [](const std::pair<std::string, int> &pair) { return pair; });
  std::move(game_score.begin(), game_score.end(), score.begin());
  std::sort(score.begin(), score.end(), [](auto a, auto b) { return a.second >= b.second; });
  const int max_score = score[0].second;
  for (auto &s : score) {
    if (s.second == max_score) 
      user.print_winner(s.first);
  }
}

namespace {
  Creator<Business_logic, Competition> a{"fast", []() -> auto { return new Competition(Mode::FAST); }};
  Creator<Business_logic, Competition> b{"detailed", []() -> auto { return new Competition(Mode::DETAILED); }};
  Creator<Business_logic, Tournament> c{"tournament"};
}

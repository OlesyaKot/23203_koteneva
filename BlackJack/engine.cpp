#include "factory.h"
#include "strategy.h"
#include "engine.h"
#include "user_interface.h"
// #include "desk.h"
#include <functional>
#include <vector>

// можно в main сделать стратегии и передать в движок. 
void Competition::game(std::vector<std::string> strategies) {
  User_interface user;
  try {
    s1 = Factory<std::string, Strategy, std::function<Strategy*()>>::get_instance()->create_by_name(strategies[0]);  // ? нужно ли обрабатывать, если не пришел объект из фабрики
    s2 = Factory<std::string, Strategy, std::function<Strategy*()>>::get_instance()->create_by_name(strategies[1]);  // ???????
  } 
  catch (std::bad_alloc &a) {
    user.print_error();
    return;
  }
  //std::unique_ptr<Desk> desk = Factory<std::string, Desk>::get_instance()->create_by_name(deskmode);
  //std::vector<int> cards = desk->get_desk();

  for (int i = 0; i < 21; i++) {
    for_s1.push_back(get_card());
    for_s2.push_back(get_card());
  }

  s1->see_opposite_card(for_s1[0]);
  s2->see_opposite_card(for_s2[0]);

  while (1) {
    if (mode == Mode::DETAILED) 
      user.print_curr_round();

    if (flag1) {
      flag1 = s1->next_move(for_s1.back());
      total_sum_1 += for_s1.back();

      if (mode == Mode::DETAILED)
        user.print_curr_move(s1->get_name(), for_s1.back(), total_sum_1, s1->get_move());
    }

    if (total_sum_2 == 21 || total_sum_1 > 21) {
      user.print_res(s2->get_name(), total_sum_2);
      return;
    }

    if (flag2) {
      flag2 = s2->next_move(for_s2.back());
      total_sum_2 += for_s2.back();

      if (mode == Mode::DETAILED)
        user.print_curr_move(s2->get_name(), for_s2.back(), total_sum_2, s2->get_move());
    }

    if (total_sum_1 == 21 || total_sum_2 > 21) {
      user.print_res(s1->get_name(), total_sum_1);
      return;
    }

    if (!flag1 && !flag2) {
      if (total_sum_1 > total_sum_2) {
        user.print_res(s1->get_name(), total_sum_1);
        return;
      }
      user.print_res(s2->get_name(), total_sum_2);
      return;
    }
  }
  for_s1.pop_back();
  for_s2.pop_back();
};

void Tournament::game(std::vector<std::string> strategies) {
  User_interface user;
  int game_score[strategies.size() + 1];

  for (int i = 0; i < strategies.size(); ++i) {
    for (int j = 0; j < strategies.size(); ++j) {
      if (i == j) continue;
      user.print_pair(strategies[i], strategies[j]);
      Competition c(Mode::FAST);
      std::vector<std::string> v;
      v.push_back(strategies[i]);
      v.push_back(strategies[j]);
      c.game(v);
    }
  }
}

namespace {
  Creator<Business_logic, Competition> a{"fast", []() -> auto { return new Competition(Mode::FAST); }};
  Creator<Business_logic, Competition> b{"detailed", []() -> auto { return new Competition(Mode::DETAILED); }};
  Creator<Business_logic, Tournament> c{"tournament"};
}

static constexpr int FACT_CARD = 11;
  int get_card() {
    while (true) {
      int c = rand() % FACT_CARD;
      if (c == 0) continue;
      return c;
     }
}

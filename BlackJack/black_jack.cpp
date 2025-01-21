#include <boost/program_options.hpp>
#include <iostream>
#include <string>
#include <vector>

#include "engine.h"
#include "factory.h"
#include "user_interface.h"
#include "deck.h"

namespace po = boost::program_options;

int main(int argc, char** argv) {
  if (argc < 2) {
    std::cout << "Not enought arguments" << std::endl;
    return 0;
  }

  std::vector<std::string> strategies;
  std::string config;
  std::string mode;

  po::options_description desc("Allowed options");
  desc.add_options()("help", "follow the instractions below")
      ("strategies", po::value<std::vector<std::string>>(&strategies)->value_name("strategy_1 strategy_2 [strategy_3 ... 7]"), "set strategies")
      ("mode", po::value<std::string>(&mode)->value_name("[detailed|fast|tournament]"), "set gamemode")
      ("deckmode", po::value<std::string>()->value_name("[simple|multiple]")->default_value("simple"), "set deckmode")
      ("N", po::value<int>()->value_name("[number of decks]")->default_value(4), "set N")
      ("config", po::value<std::string>(&config)->value_name("[configure path]"), "set configs");

  po::positional_options_description pos;
  pos.add("strategies", -1);

  po::variables_map vm;
  po::store(po::command_line_parser(argc, argv).options(desc).positional(pos).run(), vm);
  po::notify(vm);

  if (vm.count("help") || strategies.size() < 2) {
    std::cout << desc << std::endl;
    return 1;
  }

  if (!vm.count("mode")){
    mode = strategies.size() > 2 ? "tournament" : "detailed";
  }

  if (strategies.size() > 2 && mode != "tournament") {
    std::cout << "mode tournament for 3 and more strategies" << std::endl;
    std::cout << std::endl;
    std::cout << desc << std::endl;
    return 1;
  }

  User_interface user;
  if (vm.at("N").as<int>() <= 0){
    user.print_deck_error();
    return 0;
  }

  std::vector<std::unique_ptr<Strategy>> u_strategy;

  for (const auto &strategy : strategies){
    std::unique_ptr<Strategy> s;
    try {
      s = Factory<std::string, Strategy, std::function<Strategy *()>>::get_instance()->create_by_name(strategy);

    } catch (const std::bad_alloc &a) {
      user.print_error();
      return 0;
    } catch (const std::invalid_argument &a) {
      user.print_invalid_arg_error(a.what());
      return 0;
    } catch (const std::out_of_range &a) {
      user.print_invalid_arg_error("Invalid name of " + strategy);
      return 0;
    };
    
    try {
      s->get_data_from_config(config);
    } catch (std::ios_base::failure &f) {
      user.print_config_error();
      return 0;
    }
    u_strategy.push_back(std::move(s));
  }

  std::unique_ptr<Deck> d;
  try {
    d = Factory<std::string, Deck, std::function<Deck *(int)>>::get_instance()->create_by_name(vm.at("deckmode").as<std::string>(), vm.at("N").as<int>());
  } catch (const std::bad_alloc &a) {
    user.print_error();
    return 0;
  } catch (const std::invalid_argument &a) {
    user.print_invalid_arg_error(a.what());
    return 0;
  } catch (const std::out_of_range &a) {
    user.print_invalid_arg_error("Invalid name of " + vm.at("deckmode").as<std::string>());
    return 0;
  };

  std::vector<Strategy *> strategy_ptr;
  std::transform(u_strategy.begin(), u_strategy.end(), std::back_inserter(strategy_ptr), [](auto& p) { return p.get(); });

  std::unique_ptr<Business_logic> b;
  try {
    b = Factory<std::string, Business_logic, std::function<Business_logic*()>>::get_instance()->create_by_name(vm.at("mode").as<std::string>());
  } catch (const std::bad_alloc &a) {
    user.print_error();
    return 0;
  } catch (const std::invalid_argument &a) {
    user.print_invalid_arg_error(a.what());
    return 0;
  } catch (const std::out_of_range &a) {
    user.print_invalid_arg_error("Invalid name of " + vm.at("mode").as<std::string>());
    return 0;
  };

  b->game(std::move(strategy_ptr), d.get());
}

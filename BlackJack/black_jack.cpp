#include <boost/program_options.hpp>
#include <iostream>
#include <string>
#include <vector>

#include "factory.h"
#include "engine.h"

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
    mode = strategies.size() > 2 ? "tournamet" : "detailed";
  }

  if (strategies.size() > 2 && mode != "tournament") {
    std::cout << "mode tournament for 3 and more strategies" << std::endl;
    std::cout << std::endl;
    std::cout << desc << std::endl;
    return 1;
  }

  auto b = Factory<std::string, Business_logic, std::function<Business_logic*()>>::get_instance()->create_by_name(vm.at("mode").as<std::string>());
  b->game(std::move(strategies), vm.at("deckmode").as<std::string>(), std::move(config), vm.at("N").as<int>());
}

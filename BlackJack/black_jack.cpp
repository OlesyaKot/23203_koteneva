#include <iostream>
#include <string>
#include <vector>
#include <boost/program_options.hpp>

#include "factory.h"
#include "strategy.h"
#include "engine.h"

namespace po = boost::program_options;

int main(int argc, char** argv) {
  if (argc < 2) {
    std::cout << "Not enought arguments" << std::endl;
    return 0;
  }

  po::options_description desc("Allowed options");
  desc.add_options()("help", "follow the instractions below")
      ("strategies", po::value<std::vector<std::string>>()->value_name("strategy_1 strategy_2 [strategy_3 ....]"), "set strategies")
      ("mode", po::value<std::string>()->value_name("--mode=[detailed|fast|tournament]"), "set gamemode")
      ("deckmode", po::value<std::string>()->value_name("--deckmode=[simple|any number except zero: 1|2|3| ...]"), "set deckmode");
  //    ("configs", po::value<std::string>(), "set configs");

  po::positional_options_description pos;
  pos.add("strategies", -1);

  po::variables_map vm;
  po::store(po::command_line_parser(argc, argv).options(desc).positional(pos).run(), vm);
  po::notify(vm);

  std::vector<std::string> strat = vm.at("strategies").as<std::vector<std::string>>();

  if (vm.count("help") || vm.count("mode") == 0 || strat.size() < 2 || vm.count("deckmode") == 0) {
    std::cout << desc << std::endl;
    return 1;
  }

  if (strat.size() > 2 && vm.at("mode").as<std::string>() != "tournament") {
    std::cout << "mode tournament for 3 and more strategies" << std::endl;
    std::cout << std::endl;
    std::cout << desc << std::endl;
    return 1;
  }

  auto b = Factory<std::string, Business_logic, std::function<Business_logic*()>>::get_instance()->create_by_name(vm.at("mode").as<std::string>());
  b->game(strat);
  // std::move
  // если в дальнейшем они еще нужны, то передавать так: const &
}

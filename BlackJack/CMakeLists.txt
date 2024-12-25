cmake_minimum_required(VERSION 3.5)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

project("gtest")

find_package(Boost CONFIG COMPONENTS program_options REQUIRED)
include_directories(${Boost_INCLUDE_DIR})
enable_testing()
add_executable(blackjack black_jack.cpp engine.cpp user_interface.cpp deck.cpp strategy_1.cpp 
               strategy_2.cpp strategy_3.cpp strategy_4.cpp strategy_5.cpp strategy_6.cpp strategy_7.cpp)
target_compile_options(blackjack PRIVATE -Wall -Wextra -g -fsanitize=address,leak)
target_link_options(blackjack PRIVATE -fsanitize=address,leak)
target_link_libraries(blackjack ${Boost_LIBRARIES})


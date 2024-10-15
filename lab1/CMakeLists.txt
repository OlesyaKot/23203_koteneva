cmake_minimum_required(VERSION 3.0)

project("gtest")

enable_testing()
add_subdirectory(googletest)
add_executable(flat_map_test flatmap.cpp test.cpp)
target_compile_options(flat_map_test PRIVATE --coverage -Wall -fsanitize=address)
target_link_options(flat_map_test PRIVATE -lgcov --coverage -fsanitize=address)
target_link_libraries(flat_map_test gtest_main gmock_main)


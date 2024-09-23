cmake_minimum_required(VERSION 3.0)  
  
project("gtest")  
  
enable_testing()  
add_subdirectory(googletest)  
add_executable(test_app test.cpp)  
#здесь вместо main.cpp файл с тестами  
target_link_libraries(test_app gtest_main gmock_main)


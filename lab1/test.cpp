#include "flatmap.cpp"
#include "googletest/googletest/include/gtest/gtest.h"

TEST(Group1, IsEmpty){
    Flat_map f;
    EXPECT_TRUE(f.empty());
    f.clear();
    f.insert("Alla", {25, 185});
    EXPECT_FALSE(f.empty());
    f.insert("Anna", {35, 187});
    f.insert("Fred", {85, 188});
    f.insert("George", {10, 185});
    f.insert("Kolya", {55, 160});
    f.insert("Harry", {24, 170});
    f.insert("Bob", {55, 195});
    f.insert("Ida", {20, 155});
    f.insert("Teo", {8, 105});
    f.insert("Mia", {28, 145});
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    f.insert("Tim", {8, 105});
    f.insert("Mark", {28, 145});
    f.insert("Ron", {85, 188});
    f.insert("Rimus", {10, 185});
    f.insert("Severus", {55, 160});
    f.insert("Rosa", {24, 170});
    EXPECT_FALSE(f.empty());
    f.clear();
    EXPECT_TRUE(f.empty());
};

TEST(Group1, Insert){
    Flat_map f;
    EXPECT_FALSE(f.insert("Anna", {15, 160}));
    EXPECT_TRUE(f.insert("Anna", {15, 160}));
};

TEST(Group1, IsEquals){
    Flat_map f1;
    Key k = "Anna";
    Value v = {15, 160};
    f1.insert(k, v);
    Flat_map f2;
    f2.insert(k, v);
    EXPECT_TRUE(f1 == f2);
    f1.insert("Ron", {85, 188});
    f1.insert("Rimus", {10, 185});
    f1.insert("Severus", {55, 160});
    f1.insert("Rosa", {24, 170});
    EXPECT_TRUE(f1 != f2);
    f2.insert("Ron", {85, 188});
    f2.insert("Rimus", {10, 185});
    f2.insert("Pol", {55, 160});
    f2.insert("Emma", {24, 170});
    EXPECT_TRUE(f1 != f2);
    EXPECT_FALSE(f1 == f2);
};

TEST(Group1, Contains){
    Flat_map f;
    f.insert("Anna", {15, 160});
    EXPECT_TRUE(f.contains("Anna"));
    Key k1 = "Bob";
    EXPECT_FALSE(f.contains(k1));
};

TEST(Group1, At){
    Flat_map f;
    f.insert("Anna", {15, 160});
    f.insert("Mia", {28, 145});
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    f.insert("Tim", {8, 105});
    f.insert("Mark", {28, 145});
    f.insert("Ron", {85, 188});
    f.insert("Remus", {10, 185});
    f.insert("Severus", {55, 160});
    f.insert("Rosa", {24, 170});
    Flat_map f1 = f; // конструктор копирования
    EXPECT_TRUE(f.at("Rosa").age == 24);
    EXPECT_TRUE(f.at("Rosa").height == 170);
    EXPECT_THROW(f.at("Roza"), std::runtime_error);
};

TEST(Group1, Erase){
    Flat_map f;
    EXPECT_TRUE(f.erase("Anna"));
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    f.insert("Tim", {8, 105});
    EXPECT_FALSE(f.erase("Ula"));
    f.insert("Mark", {28, 145});
    f.insert("Ron", {85, 188});
    f.insert("Remus", {10, 185});
    f.insert("Severus", {55, 160});
    f.insert("Rosa", {24, 170});
    EXPECT_TRUE(f.erase("Emma"));
};

TEST(Group1, Size){
    Flat_map f;
    EXPECT_TRUE(f.size() == 0);
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    f.insert("Tim", {8, 105});
    EXPECT_TRUE(f.size() == 5);
    f.insert("Mark", {28, 145});
    f.insert("Ron", {85, 188});
    f.insert("Remus", {10, 185});
    f.insert("Severus", {55, 160});
    f.insert("Rosa", {24, 170});
    f.insert("Anna", {15, 160});
    f.insert("Mia", {28, 145});
    f.insert("Alla", {25, 185});
    f.insert("Bob", {55, 195});
    f.insert("Ida", {20, 155});
    f.insert("Teo", {8, 105});
    f.insert("Ben", {25, 195});
    f.insert("Irina", {20, 155});
    f.insert("Ted", {30, 177});
    EXPECT_TRUE(f.size() == 19);
    f.erase("Mark");
    EXPECT_TRUE(f.size() == 18);
    f.erase("Max");
    EXPECT_TRUE(f.size() == 18);
    f.clear();
    EXPECT_TRUE(f.size() == 0);
};

int main(int argc, char **argv)
{
    ::testing::InitGoogleTest(&argc, argv);

    return RUN_ALL_TESTS();
}

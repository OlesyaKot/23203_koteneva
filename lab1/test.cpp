#include "flatmap.h"
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
    EXPECT_TRUE(f.insert("Anna", {35, 187}));
    EXPECT_FALSE(f.insert("Fred", {85, 188}));
    EXPECT_FALSE(f.insert("George", {10, 185}));
    EXPECT_FALSE(f.insert("Kolya", {55, 160}));
    EXPECT_FALSE(f.insert("Harry", {24, 170}));
    EXPECT_FALSE(f.insert("Bob", {55, 195}));
    EXPECT_FALSE(f.insert("Ida", {20, 155}));
    EXPECT_FALSE(f.insert("Teo", {8, 105}));
    EXPECT_FALSE(f.insert("Mia", {28, 145}));
    EXPECT_FALSE(f.insert("Tolya", {55, 160}));
    EXPECT_FALSE(f.insert("Ula", {24, 170}));
    EXPECT_FALSE(f.insert("Elle", {55, 195}));
    EXPECT_FALSE(f.insert("Helen", {20, 155}));
    EXPECT_FALSE(f.insert("Tim", {8, 105}));
    EXPECT_FALSE(f.insert("Mark", {28, 145}));
    EXPECT_FALSE(f.insert("Ron", {85, 188}));
    EXPECT_FALSE(f.insert("Rimus", {10, 185}));
    EXPECT_FALSE(f.insert("Severus", {55, 160}));
    EXPECT_FALSE(f.insert("Rosa", {24, 170}));
    f.erase("Elle");
    EXPECT_FALSE(f.insert("Elle", {55, 195}));
};

TEST(Group1, IsEquals){
    Flat_map f1;
    Key k = "Anna";
    Value v = {15, 160};
    f1.insert(k, v);
    Flat_map f2;
    f2.insert(k, v);
    EXPECT_TRUE(f1 == f2);
    EXPECT_FALSE(f1 != f2);
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
    f2 = f1;
    EXPECT_TRUE(f1 == f2);
};

TEST(Group1, Contains){
    Flat_map f;
    f.insert("Anna", {15, 160});
    f.insert("Ron", {85, 188});
    f.insert("Rimus", {10, 185});
    f.insert("Pol", {55, 160});
    f.insert("Emma", {24, 170});
    EXPECT_TRUE(f.contains("Anna"));
    EXPECT_TRUE(f.contains("Pol"));
    EXPECT_FALSE(f.contains("anna"));
    f.erase("Ron");
    EXPECT_FALSE(f.contains("Ron"));
    EXPECT_FALSE(f.contains("Bob"));
    f.clear();
    EXPECT_FALSE(f.contains("Emma"));
};

TEST(Group1, At){
    Flat_map f;
    f.insert("Anna", {15, 160});
    f.insert("Mia", {28, 145});
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    EXPECT_THROW(f.at("Ron"), std::runtime_error);
    EXPECT_EQ(f.at("Mia").age, 28);
    EXPECT_FALSE(f.at("Mia").height == 150);
    f.erase("Mia");
    EXPECT_THROW(f.at("Mia"), std::runtime_error);
    f.insert("Tim", {8, 105});
    f.insert("Mark", {28, 145});
    EXPECT_NE(f.at("Elle").age, 45);
    f.insert("Ron", {85, 188});
    f.insert("Remus", {10, 185});
    f.insert("Severus", {55, 160});
    f.insert("Rosa", {24, 170});
    Flat_map f1 = f;
    EXPECT_EQ(f1.at("Rosa").age, 24);
    EXPECT_EQ(f1.at("Rosa").height, 170);
    EXPECT_THROW(f1.at("Roza"), std::runtime_error);
};

TEST(Group1, ConstAt){
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
    f = *(&f);
    const Flat_map f1 = f;
    EXPECT_EQ(f1.at("Rosa").age, 24);
    EXPECT_EQ(f1.at("Rosa").height, 170);
    EXPECT_THROW(f1.at("Roza"), std::runtime_error);
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

    f.insert("Anna", {15, 160});
    f.insert("Fred", {85, 188});
    f.insert("George", {10, 185});
    f.insert("Kolya", {55, 160});
    f.insert("Harry", {24, 170});
    f.insert("Bob", {55, 195});
    f.insert("Ida", {20, 155});
    EXPECT_TRUE(f.erase("Teo"));
    EXPECT_TRUE(f.erase("Emma"));
    f.empty();
    EXPECT_TRUE(f.erase("Ann"));
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
    EXPECT_NE(f.size(), 0);
    EXPECT_NE(f.size(), 16);
    EXPECT_EQ(f.size(), 19);
    f.erase("Mark");
    EXPECT_EQ(f.size(), 18);
    f.erase("Max");
    EXPECT_EQ(f.size(), 18);
    f.clear();
    EXPECT_EQ(f.size(), 0);
};

TEST(Group1, OperatorSqrtScobe){
    Flat_map f;
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
    EXPECT_EQ(f["Elle"].age, 55);
    EXPECT_EQ(f["Elle"].height, 195);
    f.erase("Elle");
    EXPECT_EQ(f["Elle"].age, 0);
    EXPECT_EQ(f["Elle"].height, 0);
    EXPECT_EQ(f["Emma"].age, 0);
    EXPECT_NE(f["Emma"].height, 100);
};

TEST(Group1, ConstructorMove){
    Flat_map f;
    f.insert("Tolya", {55, 160});
    f.insert("Ula", {24, 170});
    f.insert("Elle", {55, 195});
    f.insert("Helen", {20, 155});
    f.insert("Tim", {8, 105});
    Flat_map f1 = std::move(f);
    f = std::move(*(&f));
    EXPECT_TRUE(f1.size() == 5);
    EXPECT_EQ(f1["Tim"].age, 8);
    Flat_map f2;
    f2.insert("Tolya", {55, 160});
    f2.insert("Ula", {24, 170});
    f2.insert("Elle", {55, 195});
    f2.insert("Helen", {20, 155});
    f2.insert("Tim", {8, 105});
    f2.insert("Mark", {28, 145});
    f2.insert("Ron", {85, 188});
    f2.insert("Remus", {10, 185});
    f2.insert("Severus", {55, 160});
    f2.insert("Rosa", {24, 170});
    f2.insert("Anna", {35, 187});
    f2.insert("Fred", {85, 188});
    f2.insert("George", {10, 185});
    f2.insert("Kolya", {55, 160});
    f2.insert("Harry", {24, 170});
    f2.insert("Bob", {55, 195});
    f2.insert("Ida", {20, 155});
    Flat_map f3 = std::move(f2);
    EXPECT_TRUE(f3.size() == 17);
    EXPECT_EQ(f3["Ida"].height, 155);
};

TEST(Group1, Swap){
    Flat_map f;
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
    Flat_map f1;
    f1.swap(f);
    EXPECT_EQ(f1.size(), 10);
    EXPECT_EQ(f1.at("Rosa").height, 170);
    f1.erase("Ula");
    EXPECT_EQ(f1.size(), 9);
};

TEST(Group1, OperatorAssign){
    Flat_map f;
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
    Flat_map f1;
    f1 = f;
    EXPECT_EQ(f1.size(), 10);
    EXPECT_EQ(f1.at("Rosa").height, 170);
    f1.erase("Ula");
    f1.insert("George", {10, 185});
    f1.insert("Kolya", {55, 160});
    f1.insert("Harry", {24, 170});
    f1.insert("Bob", {55, 195});
    f1.insert("Ida", {20, 155});
    EXPECT_EQ(f1.size(), 14);
    EXPECT_TRUE(f1.contains("Tim"));
    EXPECT_FALSE(f1.empty());
    f1.clear();
    EXPECT_TRUE(f1.size() == 0);
};

int main(int argc, char **argv){
    ::testing::InitGoogleTest(&argc, argv);

    return RUN_ALL_TESTS();
}

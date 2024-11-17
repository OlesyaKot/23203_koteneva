#include "flatmap.h"
#include "googletest/googletest/include/gtest/gtest.h"

#include <string>

TEST(Group1, IsEmpty){
    Flat_map f;
    EXPECT_TRUE(f.empty());
    f.clear();
    f.insert("Alla", {25, 185});
    EXPECT_FALSE(f.empty());
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f.insert("Rimus" + std::to_string(i), {10 + i, 120 + i}));
    }
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
    EXPECT_FALSE(f.insert("Elle", {55, 195}));
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f.insert("Rimus" + std::to_string(i), {10 + i, 120 + i}));
    }
    f.erase("Elle");
    EXPECT_FALSE(f.insert("Elle", {55, 195}));
    f.clear();
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f.insert("Anna" + std::to_string(i), {10 + i, 120 + i}));
    }
    EXPECT_TRUE(f.insert("Anna2", {12, 122}));
};

TEST(Group1, IsEquals){
    Flat_map f1, f2;
    f1.insert("Anna", {15, 160});
    f2.insert("Anna", {15, 160});
    EXPECT_TRUE(f1 == f2);
    EXPECT_FALSE(f1 != f2);
    f1.insert("Ron", {85, 188});
    f1.insert("Rimus", {10, 185});
    f1.insert("Severus", {55, 160});
    f1.insert("Rosa", {24, 170});
    EXPECT_TRUE(f1 != f2);
    f2.insert("Ron", {40, 188});
    f2.insert("Rimus", {10, 185});
    f2.insert("Severus", {55, 160});
    f2.insert("Rosa", {24, 170});
    EXPECT_TRUE(f1 != f2);
    EXPECT_FALSE(f1 == f2);
    f2 = f1;
    EXPECT_TRUE(f1 == f2);

    Flat_map f3, f4;
    f3.insert("Alice", {12, 138});
    f4.insert("Alice", {12, 138});
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f4.insert("Rimus" + std::to_string(i), {10 + i, 120 + i}));
    }
    EXPECT_EQ(f4.size(), 21);
    EXPECT_FALSE(f3 == f4);
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_EQ(f4.erase("Rimus" + std::to_string(i)), 0);
    }
    EXPECT_TRUE(f3 == f4);

    Flat_map f5, f6, f7;
    EXPECT_TRUE(f5 == f6);
    for (unsigned int i = 0; i < 200; ++i){
        f6.insert("Anna" + std::to_string(i), {10 + i, 120 + i});
    }
    f6.clear();
    EXPECT_TRUE(f5 == f6);

    for (unsigned int i = 0; i < 100; ++i) {
        f7.insert("Ann" + std::to_string(i), {20 + i, 120 + i});
    }

    f7.clear();
    EXPECT_TRUE(f6 == f7);
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
    EXPECT_TRUE(f.contains("Ron"));
    EXPECT_FALSE(f.erase("Ron"));
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
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f.insert("Harry" + std::to_string(i), {10 + i, 120 + i}));
    }

    f = *(&f);
    const Flat_map f1 = f;
    EXPECT_EQ(f1.at("Harry10").age, 20);
    EXPECT_EQ(f1.at("Harry15").height, 135);
    EXPECT_THROW(f1.at("Roza"), std::runtime_error);
};

TEST(Group1, Erase){
    Flat_map f;
    EXPECT_TRUE(f.erase("Anna"));
    for (unsigned int i = 0; i < 100; ++i){
        EXPECT_FALSE(f.insert("Rimus" + std::to_string(i), {10 + i, 120 + i}));
    }
    EXPECT_TRUE(f.erase("Ula"));
    EXPECT_FALSE(f.erase("Rimus80"));
    EXPECT_TRUE(f.erase("Rimus80"));
    EXPECT_TRUE(f.erase("Teo"));
    EXPECT_TRUE(f.erase("Emma"));
    f.empty();
    EXPECT_TRUE(f.erase("Ann"));
};

TEST(Group1, Size){
    Flat_map f;
    EXPECT_TRUE(f.size() == 0);
    for (unsigned int i = 0; i < 10; ++i){
        f.insert("Anna" + std::to_string(i), {10 + i, 120 + i});
    }
    EXPECT_TRUE(f.size() == 10);
    for (unsigned int i = 0; i < 50; ++i){
        f.insert("Bob" + std::to_string(i), {10 + i, 120 + i});
    }
    EXPECT_NE(f.size(), 0);
    EXPECT_NE(f.size(), 16);
    EXPECT_EQ(f.size(), 60);
    f.erase("Bob0");
    EXPECT_EQ(f.size(), 59);
    f.erase("Anna5");
    EXPECT_EQ(f.size(), 58);
    Flat_map f1 = f;
    EXPECT_EQ(f1.size(), 58);
    f1.erase("Anna1");
    EXPECT_EQ(f1.size(), 57);
    EXPECT_NE(f.size(), 57);
    f.clear();
    EXPECT_EQ(f.size(), 0);
    EXPECT_NE(f1.size(), 0);
    f1.clear();
    EXPECT_EQ(f1.size(), 0);
};

TEST(Group1, OperatorSqrtScobe){
    Flat_map f;
    for (unsigned int i = 0; i < 50; ++i){
        f.insert("Bob" + std::to_string(i), {10 + i, 120 + i});
    }
    EXPECT_EQ(f["Bob8"].age, 18);
    EXPECT_EQ(f["Bob8"].height, 128);
    f.erase("Bob15");
    EXPECT_EQ(f["Bob15"].age, 0);
    EXPECT_EQ(f["Bob15"].height, 0);
    EXPECT_EQ(f["Emma"].age, 0);
    EXPECT_EQ(f["Bob16"].height, 136);
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
    for (unsigned int i = 0; i < 17; ++i){
        f2.insert("Harry" + std::to_string(i), {10 + i, 120 + i});
    }
    Flat_map f3 = std::move(f2);
    EXPECT_TRUE(f3.size() == 17);
    EXPECT_EQ(f3["Harry5"].height, 125);
};

TEST(Group1, Swap){
    Flat_map f;
    for (unsigned int i = 0; i < 10; ++i){
        f.insert("Ann" + std::to_string(i), {10 + i, 120 + i});
    }

    Flat_map f1;
    f1.swap(f);
    EXPECT_EQ(f1.size(), 10);
    EXPECT_EQ(f1.at("Ann3").height, 123);
    f1.erase("Ann8");
    EXPECT_EQ(f1.size(), 9);
};

TEST(Group1, OperatorAssign){
    Flat_map f;
    for (unsigned int i = 0; i < 10; ++i){
        f.insert("Kate" + std::to_string(i), {10 + i, 120 + i});
    }

    Flat_map f1;
    f1 = f;
    EXPECT_EQ(f1.size(), 10);
    EXPECT_EQ(f1.at("Kate5").height, 125);
    f1.erase("Kate6");
    for (unsigned int i = 0; i < 20; ++i){
        f1.insert("Ron" + std::to_string(i), {10 + i, 120 + i});
    }
    EXPECT_EQ(f1.size(), 29);
    EXPECT_TRUE(f1.contains("Ron8"));
    EXPECT_FALSE(f1.empty());
    f1.clear();
    EXPECT_TRUE(f1.size() == 0);

    Flat_map f2;
    for (unsigned int i = 0; i < 400; ++i){
        EXPECT_FALSE(f.insert("Anna" + std::to_string(i), {10 + i, 120 + i}));
    }
    Flat_map f3;
    f2.clear();
    f3 = f2;
    EXPECT_EQ(f3.size(), 0);
    for (unsigned int i = 0; i < 9; ++i){
        EXPECT_FALSE(f3.insert("Ella" + std::to_string(i), {10 + i, 120 + i}));
    }
    EXPECT_EQ(f3.size(), 9);
    for (unsigned int i = 0; i < 20; ++i){
        EXPECT_FALSE(f3.insert("Kate" + std::to_string(i), {10 + i, 120 + i}));
    }
    EXPECT_EQ(f3.size(), 29);
    f3.erase("Ella4");
    EXPECT_NE(f3.size(), 29);
};

int main(int argc, char **argv){
    ::testing::InitGoogleTest(&argc, argv);

    return RUN_ALL_TESTS();
}

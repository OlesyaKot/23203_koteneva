#include "flatmap.cpp"
#include "googletest/googletest/include/gtest/gtest.h"

using namespace std;

TEST(Group1, IsEmptyTest){
    Flat_map f;
    ASSERT_TRUE(f.empty());
};

TEST(Group1, InsertTest1){
    Flat_map f;
    Key k = "Anna";
    Value v = {15, 160};
    ASSERT_FALSE(f.insert(k, v));
};

TEST(Group1, InsertAndIsEmptyTest2){
    Flat_map f;
    Key k = "Anna";
    Value v = {15, 160};
    f.insert(k, v);
    ASSERT_FALSE(f.empty());
    ASSERT_TRUE(f.insert(k, v));
};

TEST(Group1, IsEquals){
    Flat_map f1;
    Key k = "Anna";
    Value v = {15, 160};
    f1.insert(k, v);
    Flat_map f2;
    f2.insert(k, v);
    ASSERT_FALSE(f1 == f2);
};

TEST(Group1, Contains){
    Flat_map f;
    Key k = "Anna";
    Value v = {15, 160};
    f.insert(k, v);
    ASSERT_TRUE(f.contains(k));
    Key k1 = "Bob";
    ASSERT_FALSE(f.contains(k1));
};

int main(int argc, char **argv){
    ::testing::InitGoogleTest(&argc, argv);

    return RUN_ALL_TESTS();
}

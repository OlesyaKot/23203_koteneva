#pragma once

#include <string>

using Key = std::string;

struct Value{
    unsigned age = 0;
    unsigned height = 0;

    friend bool operator==(const Value &a, const Value &b) {
        return ((a.age == b.age) && (a.height == b.height));
    };
};

// This flat_map contains data about people in two arrays - 
// in an array of strings the name of the person, in an array of structures, 
// each consisting of two values of type int, the age and height of each person.
class Flat_map{
public:

    Flat_map();

    ~Flat_map();

    Flat_map(const Flat_map &flat_map);

    Flat_map(Flat_map &&flat_map) noexcept;

    // Swap for two Flat_maps
    void swap(Flat_map &flat_map) noexcept;

    Flat_map &operator=(const Flat_map &flat_map);

    Flat_map &operator=(Flat_map &&flat_map) noexcept;
    
    // The clear function clears the contains of our Flat_map 
    void clear();

    // The erase function removes values of our Flat_map using the passed key
    // If the Flat_map does not contain the passed key, the function returns 1
    bool erase(const Key &k);

    // This function inserts the key and values into our Flat_map
    // If the Flat_map's current size equals the Flat_map's capacity,
    // it calls the expand_flat_map function
    // If characteristics are inserted by the key that already exists in the our Flat_map,
    // the function returns 1
    bool insert(const Key &k, const Value &v);

    // This function verifies if the characteristics are contained in our Flat_map by the passed key
    // If the Flat_map contains the passed key, the function returns 1
    bool contains(const Key &k);

    // This function returns the value from our Flat_map by the passed key of type const
    // If the Flat_map does not contain the passed key,
    // the function inserts this key with defults values and returns theese values
    Value &operator[](const Key &k);

    // This function returns the value from our Flat_map by the passed key of type const
    // If the Flat_map does not contain the passed key,
    // the function throws an exception "Key doesn't exist"
    Value &at(const Key &k);

    // This function returns the value from our Flat_map of type const by the passed key of type const
    // If the Flat_map does not contain the passed key,
    // the function throws an exception "Key doesn't exist"
    const Value &at(const Key &k) const;

    // This function returns the size of our Flat_map
    size_t size() const{
        return cur_size;
    };

    // This function verifies if our Flat_map is empty or not
    // If the Flat_map is empty, the function returns 1
    bool empty() const{ 
        return (cur_size == 0);
    };

    // This function verifies if two Flat_maps are equal or not
    // If th/ey are equal, the function returns 1
    friend bool operator==(const Flat_map &a, const Flat_map &b);

    // This function verifies if two Flat_maps are not equal
    // If they are not equal, the function returns 1
    friend bool operator!=(const Flat_map &a, const Flat_map &b){
        return !(a == b);
    };

private:
    void expand_flat_map();
    auto check_alloc_mem(size_t cap);

    static constexpr size_t INIT_CAP = 16;
    static constexpr size_t EXPAND_FACT = 2;
    Value *values = nullptr;
    Key *keys = nullptr;
    size_t cur_size = 0;
    size_t capacity = INIT_CAP;
};

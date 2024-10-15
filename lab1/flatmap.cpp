#include "flatmap.h"

#include <algorithm>
#include <iostream>
#include <string>


Flat_map::Flat_map(){
    check_alloc_mem(keys, values, INIT_CAP);
};

Flat_map::~Flat_map(){
    delete[] values;
    delete[] keys;
    values = nullptr;
    keys = nullptr;
};

Flat_map::Flat_map(const Flat_map &flat_map) : cur_size(flat_map.cur_size),
                                               capacity(flat_map.capacity){
    check_alloc_mem(keys, values, cur_size);

    std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
    std::copy(flat_map.values, flat_map.values + cur_size, values);
};

Flat_map::Flat_map(Flat_map &&flat_map) : values(flat_map.values),
                                          keys(flat_map.keys),
                                          cur_size(flat_map.cur_size),
                                          capacity(flat_map.capacity){
    flat_map.keys = nullptr;
    flat_map.values = nullptr;
    flat_map.cur_size = 0;
    flat_map.capacity = 0;
};

void Flat_map::swap(Flat_map &flat_map){
    std::swap(flat_map, *this);
};

Flat_map& Flat_map::operator=(const Flat_map &flat_map){ 
    if (this == &flat_map)
        return *this;

    delete[] keys;
    delete[] values;
    cur_size = flat_map.cur_size;
    capacity = flat_map.capacity;

    check_alloc_mem(keys, values, capacity);
    std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
    std::copy(flat_map.values, flat_map.values + cur_size, values);

    return *this;
};

Flat_map& Flat_map::operator=(Flat_map &&flat_map){ 
    if (this == &flat_map)
        return *this;

    delete[] keys;
    delete[] values;

    keys = flat_map.keys;
    values = flat_map.values;
    cur_size = flat_map.cur_size;
    capacity = flat_map.capacity;

    flat_map.keys = nullptr;
    flat_map.values = nullptr;
    flat_map.cur_size = 0;
    flat_map.capacity = 0;

    return *this;
};

void Flat_map::clear(){ 
    if (empty())
        return;
    delete[] keys;
    delete[] values;
    check_alloc_mem(keys, values, INIT_CAP);
    cur_size = 0;
};

bool Flat_map::erase(const Key &k){
    if (empty())
        return 1;

    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 == (keys + cur_size) || *k1 != k)
        return 1;

    int idx = std::distance(keys, k1);
    std::copy(keys + idx + 1, keys + cur_size, keys + idx);
    std::copy(values + idx + 1, values + cur_size, values + idx);
    cur_size -= 1;
    keys[cur_size] = {};
    values[cur_size] = {};
    return 0;
};

bool Flat_map::insert(const Key &k, const Value &v){
    if (cur_size == capacity)
        expand_flat_map();

    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 != (keys + cur_size) && *k1 == k) 
        return 1;

    int idx = std::distance(keys, k1);
    std::copy_backward(keys + idx, keys + cur_size, keys + cur_size + 1);
    std::copy_backward(values + idx, values + cur_size, values + cur_size + 1);
    keys[idx] = k;
    values[idx] = v;
    cur_size += 1;
    return 0;
};

bool Flat_map::contains(const Key &k){
    auto k1 = std::lower_bound(keys, keys + cur_size, k);
    return !k.compare((*k1));
};

Value& Flat_map::operator[](const Key &k){
    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    int idx = std::distance(keys, k1);
    if (k1 == (keys + cur_size) || *k1 != k)
        (void)insert(k, {});
    return values[idx];
};

Value& Flat_map::at(const Key &k){
    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 == (keys + cur_size) || *k1 != k)
        throw std::runtime_error("Key " + k + " doesn't exist");
    int idx = std::distance(keys, k1);
    return values[idx];
};

const Value& Flat_map::at(const Key &k) const {
    return const_cast<Flat_map &>(*this).at(k);
};

void Flat_map::expand_flat_map(){
    capacity *= EXPAND_FACT;
    Value *values_new = nullptr;
    Key *keys_new = nullptr;
    check_alloc_mem(keys_new, values_new, capacity);

    std::move(keys, keys + cur_size, keys_new);
    std::copy(values, values + cur_size ,values_new);
    delete[] keys;
    delete[] values;
    keys = keys_new;
    values = values_new;
};

bool operator==(const Flat_map &a, const Flat_map &b){
    if (a.cur_size != b.cur_size)
        return 0;
    if (!std::equal(a.keys, a.keys + a.cur_size, b.keys) &&
        !std::equal(a.values, a.values + a.cur_size, b.values))
            return 0;
    return 1;
};

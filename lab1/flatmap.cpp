#include "flatmap.h"

#include <algorithm>
#include <iostream>
#include <tuple>

auto Flat_map::check_alloc_mem(size_t cap){
    capacity = cap;
    auto *v = new Value[cap]; 
    Key *k = nullptr;
    try{
        k = new Key[cap]; 
    }
    catch (std::bad_alloc &a){
        delete[] values;
        throw a;
    }
    return std::make_tuple(k, v);
}

Flat_map::Flat_map(){
    std::tie(keys, values) = check_alloc_mem(INIT_CAP);
}

Flat_map::~Flat_map(){
    delete[] values;
    delete[] keys;
    values = nullptr;
    keys = nullptr;
}

Flat_map::Flat_map(const Flat_map &flat_map) : cur_size(flat_map.cur_size),
                                               capacity(flat_map.capacity){
    std::tie(keys, values) = check_alloc_mem(cur_size);

    std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
    std::copy(flat_map.values, flat_map.values + cur_size, values);
}

Flat_map::Flat_map(Flat_map &&flat_map) noexcept : values(flat_map.values),
                                          keys(flat_map.keys),
                                          cur_size(flat_map.cur_size),
                                          capacity(flat_map.capacity){
    flat_map.keys = nullptr;
    flat_map.values = nullptr;
    flat_map.cur_size = 0;
    flat_map.capacity = 0;
}

void Flat_map::swap(Flat_map &flat_map) noexcept {
    std::swap(flat_map, *this);
}

Flat_map& Flat_map::operator=(const Flat_map &flat_map){ 
    if (this == &flat_map)
        return *this;

    delete[] keys;
    delete[] values;
    cur_size = flat_map.cur_size;

    std::tie(keys, values) = check_alloc_mem(flat_map.capacity);

    std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
    std::copy(flat_map.values, flat_map.values + cur_size, values);

    return *this;
}

Flat_map& Flat_map::operator=(Flat_map &&flat_map) noexcept{ 
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
}

void Flat_map::clear(){ 
    if (empty())
        return;

    Flat_map().swap(*this);
    capacity = INIT_CAP; 
}

bool Flat_map::erase(const Key &k){
    if (empty())
        return true;

    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 == (keys + cur_size) || *k1 != k)
        return true;

    auto idx = std::distance(keys, k1);
    std::move(keys + idx + 1, keys + cur_size, keys + idx); 
    std::move(values + idx + 1, values + cur_size, values + idx); 
    cur_size -= 1;
    return false;
}

bool Flat_map::insert(const Key &k, const Value &v){
    if (cur_size == capacity)
        expand_flat_map();

    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 != (keys + cur_size) && *k1 == k) 
        return true;

    auto idx = std::distance(keys, k1);
    std::copy_backward(keys + idx, keys + cur_size, keys + cur_size + 1);
    std::copy_backward(values + idx, values + cur_size, values + cur_size + 1);
    keys[idx] = k;
    values[idx] = v;
    cur_size += 1;
    return false;
}

bool Flat_map::contains(const Key &k){
    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    return (k1 != (keys + cur_size) && k == *k1); 
}

Value& Flat_map::operator[](const Key &k){
    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    auto idx = std::distance(keys, k1);
    if (k1 == (keys + cur_size) || *k1 != k)
        (void)insert(k, {});
    return values[idx];
}

Value& Flat_map::at(const Key &k){
    Key* k1 = std::lower_bound(keys, keys + cur_size, k);
    if (k1 == (keys + cur_size) || *k1 != k)
        throw std::runtime_error("Key " + k + " doesn't exist");
    auto idx = std::distance(keys, k1);
    return values[idx];
}

const Value& Flat_map::at(const Key &k) const {
    // NOLINTNEXTLINE(cppcoreguidelines-pro-type-const-cast)
    return const_cast<Flat_map &>(*this).at(k);
}

void Flat_map::expand_flat_map(){
    Value * values_new = nullptr;
    Key * keys_new = nullptr;
    std::tie(keys_new, values_new) = check_alloc_mem(capacity * EXPAND_FACT);

    std::move(keys, keys + cur_size, keys_new);
    std::move(values, values + cur_size ,values_new);
    delete[] keys;
    delete[] values;
    keys = keys_new;
    values = values_new;
}

bool operator==(const Flat_map &a, const Flat_map &b){
    if (a.cur_size != b.cur_size)
        return false;

    return std::equal(a.keys, a.keys + a.cur_size, b.keys) &&
           std::equal(a.values, a.values + a.cur_size, b.values);
}

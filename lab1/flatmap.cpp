//#include "flatmap.h"

#include <algorithm>
#include <cstring>
#include <iostream>
#include <string>

typedef std::string Key;
typedef std::size_t size_t;

struct Value{
    unsigned age = 0;
    unsigned height = 0;
};

class Flat_map{
public:    
    Flat_map(){
        values = new Value[init_cap];
        keys = new Key[init_cap];
    };

    ~Flat_map(){
        delete[] values;
        delete[] keys;
    };

    Flat_map(const Flat_map &flat_map) : keys(nullptr), 
                                         values(nullptr), 
                                         cur_size(flat_map.cur_size), 
                                         capacity(flat_map.capacity){ // конструктор копирования
        keys = new Key[cur_size];
        values = new Value[cur_size];
        std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
        std::copy(flat_map.values, flat_map.values + cur_size, values);
    };

    Flat_map(Flat_map &&flat_map) : keys(nullptr),
                                    values(nullptr), 
                                    cur_size(0), 
                                    capacity(0){ // констурктор перемещения
        swap(flat_map);
        // keys = flat_map.keys;
        // values = flat_map.values;
        // cur_size = flat_map.cur_size;
        // capacity = flat_map.capacity;

        // flat_map.keys = nullptr;
        // flat_map.values = nullptr;
        // flat_map.cur_size = 0;
        // flat_map.capacity = 0;
    };

    void swap(Flat_map &flat_map) {
        Flat_map tmp = std::move(flat_map);
        flat_map = *this;
        *this = std::move(tmp);
    };

    Flat_map &operator=(const Flat_map &flat_map) { // оператор присваивания lvalue
        if (this == &flat_map)
            return *this;

        delete[] keys;
        delete[] values;
        cur_size = flat_map.cur_size;
        capacity = flat_map.capacity;

        Key *keys_new = new Key[capacity];
        Value *values_new = new Value[capacity];
        std::copy(flat_map.keys, flat_map.keys + cur_size, keys);
        std::copy(flat_map.values, flat_map.values + cur_size, values);

        return *this;
    };

    Flat_map &operator=(Flat_map &&flat_map) {  // оператор присваивания с перемещения rvalue
        if (this == &flat_map)
            return *this;

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

    void clear() { // отчищает контейнер 
        if (empty())
            return;
        for (size_t i = 0; i < cur_size; ++i){
            keys[i] = "";
            values[i].age = 0;
            values[i].height = 0;
        }
        cur_size = 0;
    };
    
// std::ranges  - там есть lower_bound
    bool erase(const Key &k) { // удаляет элем по заданному ключу, 0 = success
        if (empty())
            return 1;
        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        if (*k1 != k)
            return 1;

        int idx = std::distance(keys, k1);
        std::copy(keys + idx + 1, keys + cur_size, keys + idx); 
        std::copy(values + idx + 1, values + cur_size, values + idx);
        cur_size -= 1;
        keys[cur_size] = "";
        values[cur_size].age = 0;
        values[cur_size].height = 0;
        return 0;
    };

    bool insert(const Key &k, const Value &v){ // вставка  0 - success
        if (cur_size == capacity)
            expand_flat_map();

        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        if (*k1 == k)
            return 1;

        int idx = std::distance(keys, k1);

        std::copy_backward(keys + idx, keys + cur_size, keys + cur_size + 1);
        std::copy_backward(values + idx, values + cur_size, values + cur_size + 1);
        keys[idx] = k;
        values[idx] = v;
        cur_size += 1;
        return 0;
    };

    bool contains(const Key &k) { // Проверка наличия значения по заданному ключу. 1 - есть, 0 - нет
        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        return !k.compare((*k1)); // 0 - equal
    };

    Value &operator[](const Key &k) { // если нет элемента, надо вернуть ссылку куда можно поместить значения
        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        int idx = std::distance(keys, k1);
        if (*k1 != k)
            (void)insert(k, {0, 0});
        return values[idx];
    };

    Value &at(const Key &k){ // Возвращает значение по ключу. Бросает исключение при неудаче.
        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        if (*k1 != k)
            throw std::runtime_error("Key " + k + " doesn't exist");
        int idx = std::distance(keys, k1);
        return values[idx];
    };

    const Value &at(const Key &k) const{   // ?????????????????
        auto k1 = std::lower_bound(keys, keys + cur_size, k);
        if (*k1 != k)
            throw std::runtime_error("Key " + k + " doesn't exist");
        int idx = std::distance(keys, k1);
        return values[idx];
    }; 

    size_t size() const {
        return cur_size;
    };

    bool empty() const{ // 1 = yes, 0 = no
        return (cur_size == 0);
    };

    void expand_flat_map(){ 
        capacity *= 2;
        Key* keys_new = new Key[capacity];
        Value* values_new = new Value[capacity];
        std::move(keys, keys+cur_size, keys_new); // куда, откуда, сколько
        std::memcpy(values_new, values, cur_size*sizeof(Value)); // куда, откуда, сколько
        delete[] keys;
        delete[] values;
        keys = keys_new;
        values = values_new;
    };

    friend bool operator==(const Flat_map &a, const Flat_map &b) { // 0 - Yes, 1 - No
        if (a.cur_size != b.cur_size)
            return 0;
        for (size_t i = 0; i < a.cur_size; ++i){
            if (a.keys[i] != b.keys[i] || a.values[i].age != b.values[i].age || a.values[i].height != b.values[i].height)
                return 0;
        }
        return 1;
    };

    friend bool operator!=(const Flat_map &a, const Flat_map &b) { // 0 - Yes, 1 - No
        return !(a == b);
    };

    // void print(){
    //     for (int i = 0; i < cur_size; ++i){
    //         std::cout << keys[i] << " " << values[i].age << " " << values[i].height << std::endl;
    //     }
    // }

private:
    static constexpr size_t init_cap = 16;
    Value *values = nullptr;
    Key *keys = nullptr;
    size_t cur_size = 0;
    size_t capacity = init_cap;
};

// int main(){
//     return 0;
// }

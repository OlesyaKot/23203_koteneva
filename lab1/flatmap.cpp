#include <algorithm>
#include <iostream>
#include <string>

typedef std::string Key;
typedef std::size_t size_t;

typedef struct{
    unsigned age;
    unsigned height;
} Value;

class Flat_map{
public:    
    Flat_map(){
        values = new Value[init_cap];
        keys = new Key[init_cap];
        capacity = init_cap;
        cur_size = 0;
    };

    ~Flat_map(){
        delete[] values;
        delete[] keys;
    };

    Flat_map(const Flat_map &flat_map) { // конструктор копирования
        keys = new Key[flat_map.cur_size];
        values = new Value[flat_map.cur_size];
        for (int i = 0; i < flat_map.cur_size; i++){
            keys[i] = flat_map.keys[i];
            values[i].age = flat_map.values[i].age;
            values[i].height = flat_map.values[i].height;
        }
        cur_size = flat_map.cur_size;
        capacity = flat_map.capacity;
    };

    Flat_map(Flat_map &&flat_map) : keys(nullptr), values(nullptr), cur_size(0), capacity(0){ // констурктор перемещения
        keys = flat_map.keys;
        values = flat_map.values;
        cur_size = flat_map.cur_size;
        capacity = flat_map.capacity;

        flat_map.keys = nullptr;
        flat_map.values = nullptr;
        flat_map.cur_size = 0;
        flat_map.capacity = 0;
    };

    void swap(Flat_map &flat_map) {
        Flat_map tmp = std::move(flat_map);
        flat_map = *this;
        *this = std::move(tmp);
    };

    Flat_map &operator=(const Flat_map &flat_map) { // оператор присваивания lvalue  //////////////////////

        return *this;
    };

    Flat_map &operator=(Flat_map &&flat_map) {  // оператор присваивания rvalue
        if (this != &flat_map){
            delete[] keys;
            delete[] values;
        }

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
        if (!empty())
            return;
        for (size_t i = 0; i < cur_size; ++i)
        {
            keys[i] = "";
            values[i].age = 0;
            values[i].height = 0;
        }
        cur_size = 0;
    };

    bool erase(const Key &k) { // удаляет элем по заданному ключу, 0 = success
        if (!empty())
            return 1;
        auto k1 = std::lower_bound(&keys[0], &keys[cur_size], k);
        if (*k1 != k)
            throw "No such person";

        int idx = std::distance(&keys[0], k1);
        std::copy(&keys[idx + 1], &keys[cur_size], &keys[idx]);
        std::copy(&values[idx + 1], &values[cur_size], &values[idx]);
        cur_size -= 1;
        keys[cur_size] = "";
        values[cur_size].age = 0;
        values[cur_size].height = 0;
        return 0;
    };

    bool insert(const Key &k, const Value &v){ // вставка  0 - success
        expand_flat_map();
        auto k2 = std::lower_bound(&keys[0], &keys[cur_size], k);
        if (*k2 == k)
            return 1;

        auto k1 = std::upper_bound(&keys[0], &keys[cur_size], k);
        int idx = std::distance(&keys[0], k1);

        std::copy_backward(&keys[idx], &keys[cur_size], &keys[cur_size + 1]);
        std::copy_backward(&values[idx], &values[cur_size], &values[cur_size + 1]);
        keys[idx + 1] = k;
        values[idx + 1] = v;
        cur_size += 1;
        return 0;
    };

    bool contains(const Key &k) { // Проверка наличия значения по заданному ключу. 1 - есть, 0 - нет
        auto k1 = std::lower_bound(&keys[0], &keys[cur_size], k);
        return !k.compare((*k1)); // 0 - equal
    };

    Value &operator[](const Key &k) {
        auto k1 = std::lower_bound(&keys[0], &keys[cur_size], k);
        if (*k1 != k)
            throw "No such person";
        int idx = std::distance(&keys[0], k1);
        return values[idx];
    };

    Value &at(const Key &k){ // Возвращает значение по ключу. Бросает исключение при неудаче.
        auto k1 = std::lower_bound(&keys[0], &keys[cur_size], k);
        if (*k1 != k)
            throw "Error, no such name\n";
        int idx = std::distance(&keys[0], k1);
        return values[idx];
    };

    const Value &at(const Key &k) const; ////////////////////////////////////////////

    size_t size() const {
        return cur_size;
    };

    bool empty() const{ // 1 = yes, 0 = no
        return (cur_size == 0);
    };

    void expand_flat_map() { /////////////////////////////////////
        if (cur_size != capacity)
            return;
        capacity *= 2;
  
    };

    friend bool operator==(const Flat_map &a, const Flat_map &b) { // 0 - Yes, 1 - No
        if (a.cur_size != b.cur_size)
            return 1;
        for (size_t i = 0; i < a.cur_size; ++i){
            if (a.keys[i] != b.keys[i] || a.values[i].age != b.values[i].age || a.values[i].height != b.values[i].height)
                return 1;
        }
        return 0;
    };

    friend bool operator!=(const Flat_map &a, const Flat_map &b) { // 0 - Yes, 1 - No
        return !(a == b);
    };

private:
    static constexpr size_t init_cap = 16;
    Value *values;
    Key *keys;
    size_t cur_size;
    size_t capacity;
};
/*
int main(){
    Flat_map f;
    if (!f.empty())
        std::cout << "empty" << std::endl;
    return 0;
}
*/
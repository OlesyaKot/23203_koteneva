#ifndef ENGINE_H
#define ENGINE_H

#include <vector>

class Engine{
public:
    Engine(int len, int size) : prev_vect(size, false), curr_vect(size, false), len_str(len), v_size(size) {};

    void SetSell(int idx);

    std::vector<bool> GetField() {
        return curr_vect;
    }

    void Reset(){
        curr_vect.assign(v_size, false);
    }

    void Game();

private:
    std::vector<bool> prev_vect;
    std::vector<bool> curr_vect;
    int len_str;
    int v_size;
};

#endif // ENGINE_H

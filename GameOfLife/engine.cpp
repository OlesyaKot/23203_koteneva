#include "engine.h"

void Engine::SetSell(int idx){
    if (curr_vect[idx]) curr_vect[idx] = false;
    else curr_vect[idx] = true;
}

void Engine::Game(){
    std::swap(prev_vect, curr_vect);
    for (int i = 0; i < v_size; ++i){
        int counter = 0;
        counter += prev_vect[i + 1 - len_str * (i % len_str == len_str - 1)];
        counter += prev_vect[i + len_str - v_size * (v_size - i <= len_str)];
        counter += prev_vect[(i + 1 + len_str - v_size * (v_size - i <= len_str) -
                              len_str * (i % len_str == len_str - 1)) * (i != v_size-1)];
        counter += prev_vect[i - 1 + len_str * (!(i % len_str))];
        counter += prev_vect[(i - 1 + len_str - v_size * (v_size - i <= len_str) + len_str * (!(i % len_str)))];
        counter += prev_vect[i - len_str + v_size * (i < len_str)];
        counter += prev_vect[i - len_str + 1 + v_size * (i < len_str) - len_str * (i % len_str == len_str - 1)];
        counter += prev_vect[i - 1 - len_str + len_str * (!(i % len_str)) + v_size * (i < len_str)];

        if (prev_vect[i]) curr_vect[i] = 2 == counter || 3 == counter;
        else curr_vect[i] = counter == 3;
    }
}

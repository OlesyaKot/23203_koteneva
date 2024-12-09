#pragma once

#include <functional>
#include <map>
#include <memory>

template <class Key, class Type, class Func>

class Factory {
 public:
  static Factory* get_instance() {
    static Factory f;
    return &f;
  }

  std::unique_ptr<Type> create_by_name(Key name) {
    Func creator = creators_.at(name);
    auto p = creator();
    std::unique_ptr<Type> u_ptr{p}; 

    return std::move(u_ptr);
  }

  bool register_creator(const Key &name, Func creators) {
    creators_[name] = creators;
    return true;
  }

 private:
  std::map<Key, Func> creators_;
};

template <class T, class New_t>
struct Creator {
  Creator(const std::string& name, std::function<New_t*()> func = []() -> auto { return new New_t(); }) {
    Factory<std::string, T, std::function<T*()>>::get_instance()->register_creator(name, func);
  }
};

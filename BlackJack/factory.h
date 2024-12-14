#pragma once

#include <functional>
#include <map>
#include <memory>
#include <stdexcept>
#include <string>

template <class Key, class Type, class Func>

class Factory {
 public:
  static Factory* get_instance() {
    static Factory f;
    return &f;
  }

  bool register_creator(const Key &name, Func creators) {
    creators_[name] = creators;
    return true;
  }

  template <class... ParamTypes>

  std::unique_ptr<Type> create_by_name(Key name, ParamTypes&&... parameters) {
    try {
      p = creators_.at(name)(std::forward<ParamTypes>(parameters)...);
    } catch (std::out_of_range& a) {
      throw std::invalid_argument("Invalid name of " + name);
    };

    std::unique_ptr<Type> u_ptr{p};
    return u_ptr;
  }

 private:
  std::map<Key, Func> creators_;
  Type* p;
};

template <class T, class New_t, class... ParamTypes>
struct Creator {
  Creator(const std::string& name, std::function<T*(ParamTypes...)> func = [](ParamTypes... parameters) -> auto { return new New_t(parameters...); }) {
    Factory<std::string, T, std::function<T*(ParamTypes...)>>::get_instance()->register_creator(name, func);
  }
};

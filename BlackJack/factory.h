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

  Factory(const Factory& f) = delete;

  Factory& operator=(const Factory& f) = delete;

  bool register_creator(const Key &name, Func creators) {
    creators_[name] = creators;
    return true;
  }

  template <class... ParamTypes>

  std::unique_ptr<Type> create_by_name(Key name, ParamTypes&&... parameters) {
    auto p = creators_.at(name)(std::forward<ParamTypes>(parameters)...);
    std::unique_ptr<Type> u_ptr{p};   
    return u_ptr;
  }

 private:
  Factory() = default;
  std::map<Key, Func> creators_;
};

template <class T, class New_t, class... ParamTypes>
struct Creator {
  Creator(const std::string& name, std::function<T*(ParamTypes...)> func = [](ParamTypes... parameters) -> auto { return new New_t(parameters...); }) {
    Factory<std::string, T, std::function<T*(ParamTypes...)>>::get_instance()->register_creator(name, func);
  }
};

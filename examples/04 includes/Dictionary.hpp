/* --------------------------
=> File: Dictionary.hpp
=> Date: 25/12/2016
=> Description: Dictionary class (instead of map in STL)
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#ifndef DICTIONARY_HPP
#define DICTIONARY_HPP

/*
Dictionary class, intented have same interface with std::map
*/
template <class KeyType, class ValueType>
class Dictionary {
public:
    Dictionary();
    Dictionary(const Dictionary& other);
    // return value of given key, or 0 if not found
    ValueType& operator [](const KeyType& key);
    // isn't it obvious?
    int size() const { return _size; };
    // returns a pointer to the beginning of the items
    std::pair<KeyType, ValueType>* begin() const { return items; };
    // return a pointer to the end of the items
    std::pair<KeyType, ValueType>* end() const { return items + size(); };
    // gives the value at a specific key position
    ValueType at(const KeyType& key) const;
    // returns a pointer to the item of given key, or end() if not found
    std::pair<KeyType, ValueType>* find(const KeyType& key) const;
    // removes an item
    void erase(const KeyType& key);
    virtual ~Dictionary();
private:
    std::pair<KeyType, ValueType>* items;
    int _size;
};

// including implementation to make templates work
#include "Dictionary.cpp"

#endif

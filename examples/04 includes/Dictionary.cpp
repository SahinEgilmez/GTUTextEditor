/* --------------------------
=> File: Dictionary.cpp
=> Date: 25/12/2016
=> Description: Dictionary class (instead of map in STL)
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

template <class KeyType, class ValueType>
Dictionary<KeyType, ValueType>::Dictionary()
        : _size(0), items(nullptr) { /* empty constructor */ }

template <class KeyType, class ValueType>
Dictionary<KeyType, ValueType>::Dictionary(const Dictionary& other) {
    // copy constructor
    items = new std::pair<KeyType, ValueType>[other.size()];
    _size = other.size();
    for (int i = 0; i < size(); i += 1)
        items[i] = other.items[i];
}

template <class KeyType, class ValueType>
ValueType& Dictionary<KeyType, ValueType>::operator [](const KeyType& key) {
    if (find(key) != end()){  // if key found, return value
        return find(key)->second;
    }
    // else
    // create a wider temporary array
    std::pair<KeyType, ValueType>* temp;
    temp = new std::pair<KeyType, ValueType>[size() + 1];
    // copy old items
    int j = 0;
    for (; j < size(); j += 1) {
        temp[j] = items[j];
    }
    // add a new pair [key, ValueType()]
    temp[j] = std::pair<KeyType, ValueType>(key, ValueType());
    // ValueType() means default value of specified type
    // delete old items
    if (items != nullptr)
        delete [] items;
    // assign new items
    items = temp;
    // increase size
    _size += 1;
    // return value of new pair
    return items[j].second;
}

template <class KeyType, class ValueType>
void Dictionary<KeyType, ValueType>::erase(const KeyType& key) {
    _size -= 1;  // decrease size
    // find erased item position
    std::pair<KeyType, ValueType>* i = find(key);
    // shift items on right hand side to left
    for (; i != end(); i += 1)
        *i = *(i + 1);
}

template <class KeyType, class ValueType>
ValueType Dictionary<KeyType, ValueType>::at(const KeyType& key) const {
    return find(key)->second;
}

template <class KeyType, class ValueType>
std::pair<KeyType, ValueType>* Dictionary<KeyType, ValueType>
        ::find(const KeyType& key) const {
    for (int i = 0; i < size(); i += 1)
        if (items[i].first == key)
            return items + i;
    return end();
}

template <class KeyType, class ValueType>
Dictionary<KeyType, ValueType>::~Dictionary() {
    delete [] items;
}

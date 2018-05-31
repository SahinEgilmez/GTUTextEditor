/* --------------------------
=> File: BigramMap.cpp
=> Date: 14/12/2016
=> Description: Bigram class using Maps
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include <map>
#include "BigramBase.hpp"
#include "Exceptions.hpp"


#ifndef BIGRAM_MAP_HPP
#define BIGRAM_MAP_HPP

/* thanks to good design of BigramBase class template just defining a new
*  class would be sufficent by i have to add an extra constructor to make
*  _mainTester work */
template <class Type>
class BigramMap: public BigramBase< std::map<std::pair<Type, Type>, int>, Type> {
public:
    BigramMap(int) { /* empty */ };
};

#endif

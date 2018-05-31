/* --------------------------
=> File: BigramDyn.hpp
=> Date: 25/12/2016
=> Description: Bigram using Dictionary (not STL)
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include "Dictionary.hpp"
#include "BigramBase.hpp"

#ifndef BIGRAM_DYN_HPP
#define BIGRAM_DYN_HPP

/* thanks to good design of BigramBase class template just defining a new
*  class would be sufficent by i have to add an extra constructor to make
*  _mainTester work */
template <class Type>
class BigramDyn: public BigramBase<Dictionary<std::pair<Type, Type>, int>, Type> {
public:
    BigramDyn(int) { /* empty */ };
};

#endif

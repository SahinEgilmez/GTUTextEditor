/* --------------------------
=> File: Bigram.cpp
=> Date: 22/12/2016
=> Description: Base Bigram class implementation
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include "Bigram.hpp"

// operator overloading, needed to make overloading virtual
template <class Type>
std::ostream& operator <<(std::ostream& os, const Bigram<Type>& b) {
    b.print(os);
    return os;
}

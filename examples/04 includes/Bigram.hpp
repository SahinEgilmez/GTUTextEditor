/* --------------------------
=> File: bigram.hpp
=> Date: 14/12/2016
=> Description: Base Bigram class definition
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include <string>
#include <ostream>
#include "Exceptions.hpp"

#ifndef BIGRAM_HPP
#define BIGRAM_HPP

template <class Type> class Bigram;

#include "Bigram.cpp"

template <class Type>
class Bigram {
public:
    virtual void readFile(std::string filename)
        throw (FileError, ValueError) = 0;
    virtual int numGrams() const = 0;
    virtual const int numOfGrams(Type first, Type second) const = 0;
    friend std::ostream& operator<< <>(std::ostream& os, const Bigram<Type>& b);
    virtual std::pair<Type, Type> maxGrams() const = 0;
    virtual std::ostream& print(std::ostream& os) const = 0;
};


#endif

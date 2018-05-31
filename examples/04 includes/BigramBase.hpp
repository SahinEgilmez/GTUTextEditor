/* --------------------------
=> File: BigramBase.cpp
=> Date: 14/12/2016
=> Description: Base Bigram class
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include <ostream>
#include "Bigram.hpp"
#include "Exceptions.hpp"

#ifndef BIGRAM_BASE_HPP
#define BIGRAM_BASE_HPP

template <class MapClass, class Type>
class BigramBase: public Bigram<Type> {
public:
    BigramBase() { /* empty */ };
    BigramBase(const MapClass& others): bigrams(others) { /* empty */ };
    BigramBase(const BigramBase<MapClass, Type>& other);
    // reads bigrams in given file
    virtual void readFile(std::string filename)
        throw (FileError, ValueError) override;
    // returns number of calculated bigrams so far
    virtual int numGrams() const override { return bigrams.size(); };
    // returns occurence number of given bigram
    virtual const int numOfGrams(Type first, Type second) const;
    // returns pair with maximum occurence
    virtual std::pair<Type, Type> maxGrams() const;
    // prints pairs and their occurences on given stream object
    virtual std::ostream& print(std::ostream& os) const;
    // adds a new pair to bigrams
    void addPair(Type item1, Type item2);
    // removes a pair from bigrams
    void removePair(Type item1, Type item2) throw (KeyError);
    // returns map/dictionary object
    MapClass getMap() const { return bigrams; };
private:
    MapClass bigrams;
};

// including implementation to make templates work
#include "BigramBase.cpp"

#endif

/* --------------------------
=> File: BigramMap.cpp
=> Date: 18/12/2016
=> Description: Bigram implementation using Maps
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include <ostream>
#include <fstream>
#include <sstream>
#include <string>

template <class MapClass, class Type>
BigramBase<MapClass, Type>::BigramBase(const BigramBase& other)
    : BigramBase<MapClass, Type>(other.getMap()) { /* Empty */ }

template <class MapClass, class Type>
void BigramBase<MapClass, Type>::readFile(std::string filename)
        throw (FileError, ValueError) {
    std::ifstream file(filename);

    // if stream is failing before reading there should be an opening error
    if (file.fail())
        throw FileError(filename);

    std::stringstream input;
    std::string line;

    std::getline(file, line);
    file.close();    // close file
    input.str(line);

    Type first, second;  // pair variables
    input >> first;  // read first value

    while (!input.eof()) {  // reading pairs
        input >> second;
        if (input.fail())
            if (input.eof())
                continue;  // out of loop
            else {
                std::string badValue;
                input.clear();  // clearing stream to be able to read value
                input >> badValue;
                throw ValueError(badValue);
            }
        else
            addPair(first, second);  // if no error, add pair
        first = second;
    }
}

template <class MapClass, class Type>
void BigramBase<MapClass, Type>::addPair(Type item1, Type item2) {
    std::pair<Type, Type> newPair(item1, item2);
    bigrams[newPair] += 1;
}

template <class MapClass, class Type>
std::ostream& BigramBase<MapClass, Type>::print(std::ostream& os) const {
    BigramBase<MapClass, Type> copy(bigrams);  // copy this bigram
    while (copy.getMap().size() > 0) {
        auto pair = copy.maxGrams();  // find max
        int occurence = copy.numOfGrams(pair.first, pair.second);
        os << "[" << pair.first << ", " << pair.second
            << "]: " << occurence << std::endl;  // print it
        copy.removePair(pair.first, pair.second);  // and remove it
    }
    return os;
}

template <class MapClass, class Type>
const int BigramBase<MapClass, Type>::numOfGrams(Type first, Type second)
        const {
    std::pair<Type, Type> newPair(first, second);
    auto result = bigrams.find(newPair);  // if there is such pair
    if (result != bigrams.end())
        return bigrams.at(newPair);  // return occurence
    else
        return 0;  // else return 0
}

template <class MapClass, class Type>
std::pair<Type, Type> BigramBase<MapClass, Type>::maxGrams() const {
    int max = 0;
    std::pair<Type, Type> maxPair;
    for (auto item: bigrams)
        if (item.second > max) {
            max = item.second;
            maxPair = item.first;
        }
    return maxPair;
}

template <class MapClass, class Type>
void BigramBase<MapClass, Type>::removePair(Type item1, Type item2)
        throw (KeyError) {
    std::pair<Type, Type> pair(item1, item2);
    auto result = bigrams.find(pair);
    // if key exists
    if (result != bigrams.end())
        bigrams.erase(pair);  // remove pair
    else
        throw KeyError("106");  // else throw a key error with line number <--
}

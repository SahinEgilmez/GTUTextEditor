/* --------------------------
=> File: Exceptions.hpp
=> Date: 18/12/2016
=> Description: Exceptions for Bigrams
=> License: CC BY-NC 4.0
=> Notes:
-------------------------- */

#include <iostream>
#include <string>

#ifndef BIGRAM_EXCEPTIONS_HPP
#define BIGRAM_EXCEPTIONS_HPP

// Exception that is thrown on file input errors
class FileError: public std::exception {
public:
    FileError(std::string f): fileName(f) {
        message = "Error occured while reading the file: " + fileName;
    };
    inline virtual const char* what() const noexcept override {
        return message.c_str();
    };
private:
    std::string fileName;
    // using a static member because of pointer returning issues
    static std::string message;
};

// Exception that is thrown for bad values
class ValueError: public std::exception {
public:
    ValueError(std::string b): badValue(b) {
        message = "File contains inconsistent data: " + badValue;
    };
    inline virtual const char* what() const noexcept override {
        return message.c_str();
    };
private:
    std::string badValue;
    // using a static member because of pointer returning issues
    static std::string message;
};

// Exception that is thrown for non-existent keys in dictionary/map types
class KeyError: public std::exception {
public:
    KeyError(std::string l): lineNum(l) {
        message = "Key error on line: " + lineNum;
    };
    inline virtual const char* what() const noexcept override {
        return message.c_str();
    };
private:
    std::string lineNum;
    // using a static member because of pointer returning issues
    static std::string message;
};

std::string FileError::message;
std::string ValueError::message;
std::string KeyError::message;

#endif

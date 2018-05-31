#include <iostream>

void area_of_rectangle(int a, int b) {
    std::cout << "a: " << a << " b: " << b << std::endl;  // debug
    std::cout << "a * b: " << a * b << std::endl;  // debug
    std::cout << "area: " << a*b << std::endl;
}

int main() {
    area_of_rectangle(4, 8);
    return 0;
}

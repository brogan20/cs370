#include <iostream>
#include <fstream>
#include <vector>

#define BUFLEN 4

int main() {
    FILE *fp;
    char buf[BUFLEN];
    std::vector<char> bigInt;

    fp = fopen("input.txt", "r");
    // Ignoring file check

    while(fread(buf, 1, BUFLEN, fp) == BUFLEN) {
        // https://stackoverflow.com/questions/1399666/attaching-char-buffer-to-vectorchar-in-stl
        std::copy(buf,buf+BUFLEN,std::back_inserter(bigInt));
    }

    std::string s(bigInt.begin(), bigInt.end());
    std::cout << s;
}
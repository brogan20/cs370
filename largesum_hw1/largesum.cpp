#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm> // for std::fill_n, std::min, std::max
#include <future>
#include <thread>

#define BUFLEN 1024
#define FINALLEN 53 // 200 lines of 50-length 9s needs 53 chars

void addCarry(char finalBuf[FINALLEN], char remainder, char *max_ind) {
    char sum = *finalBuf - '0' + remainder;
    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        addCarry(++finalBuf, carry, max_ind);
    }
}

void add(char *buf1, char finalBuf[FINALLEN], char *max_ind) {
    char sum = (*buf1 - '0') + (*finalBuf - '0');
    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        addCarry(++finalBuf, carry, max_ind);
    }
}

void addBuf(char *buf1, char finalBuf[FINALLEN]) {
    std::string s(buf1);
    char *max_ind = finalBuf;

    while(*buf1 != '\n') {
        add(buf1, finalBuf, finalBuf);
        buf1++;
        finalBuf++;
    }
}

void addBuf_ind(char *buf1, char finalBuf[FINALLEN]) {
    std::string s(buf1);
    char *max_ind = finalBuf;
    char *len = finalBuf + FINALLEN - 1;
    while(buf1 <= len) {
        add(buf1, finalBuf, finalBuf);
        buf1++;
        finalBuf++;
    }
}

void addInts(char finalBuf[FINALLEN], std::reverse_iterator<std::vector<char>::iterator> begin, std::reverse_iterator<std::vector<char>::iterator> end) {
    char buf1[BUFLEN];
    char *max_ind = finalBuf;
    size_t len = 0;

    for (auto it = begin; it != end; ++it) { 
        char num = *it;
        buf1[len++] = num;

        if(num == '\n') {
            addBuf(buf1, finalBuf);
            
            len = 0;
        }
    }
}

int main() {
    FILE *fp;
    char buf1[BUFLEN],
        finalBuf[FINALLEN],// = {[0 ... 8191] = '0'};
        finalBuf2[FINALLEN];

    std::fill_n(finalBuf, FINALLEN, '0');
    std::fill_n(finalBuf2, FINALLEN, '0');

    fp = fopen("input.txt", "r");
    fseek(fp, 0L, SEEK_END);
    size_t file_len = ftell(fp);
    rewind(fp);

    std::vector<char> bigInt;
    bigInt.reserve(file_len);
    bigInt.push_back('\n');

    // Ignoring file check

    while(size_t count = fread(buf1, 1, BUFLEN, fp)) {
        // https://stackoverflow.com/questions/1399666/attaching-char-buffer-to-vectorchar-in-stl
        std::copy(buf1,buf1+count,std::back_inserter(bigInt));
    }

    auto halfIt = bigInt.rbegin() + file_len/2;

    // Go to nearest newline
    while(*halfIt != '\n') {
        halfIt++;
    }

    char *max_ind;
    char *largerBuf;

    if(halfIt != bigInt.rend() && std::thread::hardware_concurrency() > 1) {
        // We can do multithreading!
        std::future<void> fut = std::async (std::launch::async, addInts, finalBuf, bigInt.rbegin(), halfIt+1);
        //char *max_ind1 = addInts(finalBuf, bigInt.rbegin(), halfIt+1);
        addInts(finalBuf2, halfIt, bigInt.rend());
        fut.get();

        addBuf_ind(finalBuf, finalBuf2);
        largerBuf = finalBuf2;
    }
    else {
        addInts(finalBuf, bigInt.rbegin(), bigInt.rend());
        largerBuf = finalBuf;
    }

    max_ind = largerBuf + FINALLEN - 1;

    while(*max_ind == '0') {
        max_ind--;
    }

    if(max_ind < largerBuf) {
        printf("Full sum: 0\nFirst 10 digits: 0\n");
        return 0;
    }

    printf("Full sum: ");

    for(char *c = max_ind; c >= largerBuf; c--) {
        std::putc(*c, stdout);
    }
    std::putc('\n', stdout);

    printf("First 10 digits: ");

    int count = 0;
    for(char *c = max_ind; count < 10 && c >= largerBuf; c--, count++) {
        std::putc(*c, stdout);
    }
    std::putc('\n', stdout);

    return 0;
}
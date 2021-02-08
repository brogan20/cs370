#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm> // for std::fill_n, std::min, std::max
#include <future>
#include <thread>

#define BUFLEN 1024
#define FINALLEN 53 // 200 lines of 50-length 9s needs 53 chars

char* addCarry(char finalBuf[FINALLEN], char remainder, char *max_ind) {
    char sum = *finalBuf - '0' + remainder;
    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        max_ind = std::max(addCarry(++finalBuf, carry, max_ind), max_ind);
    }

    return std::max(max_ind, finalBuf);
}

char* add(char *buf1, char finalBuf[FINALLEN], char *max_ind) {
    char sum = (*buf1 - '0') + (*finalBuf - '0');
    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        max_ind = std::max(addCarry(++finalBuf, carry, max_ind), max_ind);
    }
    return std::max(max_ind, finalBuf);
}

char* addBuf(char *buf1, char finalBuf[FINALLEN]) {
    std::string s(buf1);
    char *max_ind = 0UL;

    while(*buf1 != '\n') {
        max_ind = std::max(add(buf1, finalBuf, finalBuf), max_ind);
        buf1++;
        finalBuf++;
    }

    return max_ind;
}

char* addBuf_ind(char *buf1, char finalBuf[FINALLEN], char *limit) {
    std::string s(buf1);
    char *max_ind = 0UL;

    while(buf1 <= limit) {
        max_ind = std::max(add(buf1, finalBuf, finalBuf), max_ind);
        buf1++;
        finalBuf++;
    }

    return max_ind;
}

char* addInts(char finalBuf[FINALLEN], std::reverse_iterator<std::vector<char>::iterator> begin, std::reverse_iterator<std::vector<char>::iterator> end) {
    char buf1[BUFLEN];
    char *max_ind = 0UL;
    size_t len = 0;

    for (auto it = begin; it != end; ++it) { 
        char num = *it;
        buf1[len++] = num;

        if(num == '\n') {
            max_ind = std::max(addBuf(buf1, finalBuf), max_ind);
            
            len = 0;
        }
    }

    return max_ind;
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
        std::future<char*> fut = std::async (std::launch::async, addInts, finalBuf, bigInt.rbegin(), halfIt+1);
        //char *max_ind1 = addInts(finalBuf, bigInt.rbegin(), halfIt+1);
        char *max_ind2 = addInts(finalBuf2, halfIt, bigInt.rend());
        char *max_ind1 = fut.get();

        if(max_ind1 > max_ind2) {
            max_ind = addBuf_ind(finalBuf2, finalBuf, max_ind2);
            largerBuf = finalBuf;
        }
        else {
            max_ind = addBuf_ind(finalBuf, finalBuf2, max_ind1);
            largerBuf = finalBuf2;
        }
    }
    else {
        max_ind = addInts(finalBuf, bigInt.rbegin(), bigInt.rend());
        largerBuf = finalBuf;
    }

    if(max_ind == 0L) {
        printf("Full sum: 0\nFirst 10 digits: 0\n");
        return 0;
    }
    while(*max_ind == '0') {
        max_ind--;
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
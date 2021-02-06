#include <iostream>
#include <fstream>
#include <vector>
#include <algorithm> // for std::fill_n, std::min, std::max

#define BUFLEN 50
#define FINALLEN 8192

long max_ind = 0;

void addCarry(char finalBuf[FINALLEN], char remainder) {
    //std::cout << "Carry: " << (int)remainder << std::endl;

    char sum = *finalBuf - '0' + remainder;
    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        addCarry(++finalBuf, carry);
    }

    max_ind = std::max(max_ind, (long)finalBuf);
}

void add(char buf1[BUFLEN], char finalBuf[FINALLEN]) {
    //std::cout << *buf1 << " + " << *finalBuf << "\n";
    
    char sum = (*buf1 - '0') + (*finalBuf - '0');
    //std::cout << "Sum: " << (int)sum << std::endl;


    *finalBuf = sum % 10 + '0';

    char carry = sum / 10;
    if(carry > 0) {
        addCarry(++finalBuf, carry);
    }
    max_ind = std::max(max_ind, (long)finalBuf);
}

void addBuf(char buf1[BUFLEN], char finalBuf[FINALLEN]) {

    std::string s(buf1);
    //std::cout << "Adding: " << s << std::endl;
    //size_t len = 0;

    while(*buf1 != '\n') {
        add(buf1, finalBuf);
        buf1++;
        finalBuf++;
        /*
        char sum = buf1[len] + finalBuf[len] - '0';
        finalBuf[len] = sum % 10 + '0';

        char carry = sum / 10;
        while(carry >= 10) {
            // Keep carrying
        }
        */

        //len++;
    }
}

int main() {
    FILE *fp;
    char buf1[BUFLEN],
        finalBuf[FINALLEN];// = {[0 ... 8191] = '0'};
    std::fill_n(finalBuf, FINALLEN, '0');

    std::vector<char> bigInt;
    bigInt.push_back('\n');

    fp = fopen("input.txt", "r");
    // Ignoring file check

    while(size_t count = fread(buf1, 1, BUFLEN, fp)) {
        // https://stackoverflow.com/questions/1399666/attaching-char-buffer-to-vectorchar-in-stl
        std::copy(buf1,buf1+count,std::back_inserter(bigInt));
    }

    
    std::string s(bigInt.begin(), bigInt.end());
    //std::cout << s;

    //return 0;
    
    
    int len = 0;

    for (std::vector<char>::reverse_iterator i = bigInt.rbegin(); i != bigInt.rend(); ++i) { 
        char num = *i;
        buf1[len++] = num;

        if(num == '\n') {
            //std::cout << "Newline: " << len << "\n";
            addBuf(buf1, finalBuf);
            
            len = 0;
        }
    }

    //std::cout << "Bop: " << max_ind-(long)finalBuf + 1 << "\n";

    std::string s2;
    s2.assign(finalBuf, max_ind-(long)finalBuf + 1);
    std::reverse(s2.begin(), s2.end());
    std::cout << "Full sum: " << s2 << std::endl;

    std::cout << "First 10 digits: ";

    int last_ind = std::min(10L, max_ind-(long)finalBuf + 1);
    for(int i = 0; i < last_ind; i++) {
        std::putc(s2.at(i), stdout);
    }
    std::putc('\n', stdout);
    /*
    for (std::vector<char>::reverse_iterator i = bigInt.rbegin(); i != bigInt.rend(); ++i) { 
        char num = *i;

    } 
    */
}
#include <fstream>
#include <sstream>
#include <string>
#include <iostream>

void rotate_right(char *buf1, char *buf2) {
    //std::cerr << "Rotating " << buf1 << " to ";

    buf2[0] = buf1[6];
    buf2[1] = buf1[7];
    buf2[2] = buf1[0];
    buf2[3] = buf1[1];
    buf2[4] = buf1[2];
    buf2[5] = buf1[3];
    buf2[6] = buf1[4];
    buf2[7] = buf1[5];
    buf2[8] = '\0';
    
    //std::cerr << buf2 << std::endl;
}

// Is buf2 to the left of buf1
int is_left(char *buf1, char *buf2) {
    return buf1[6] == buf2[2] && buf1[7] != buf2[3];
}

// Is buf2 to the right of buf1
int is_right(char *buf1, char *buf2) {
    return is_left(buf2, buf1);
}

// Is buf2 above buf1
int is_up(char *buf1, char *buf2) {
    return buf1[0] == buf2[4] && buf1[1] != buf2[5];
}

// Is buf2 below buf1
int is_down(char *buf1, char *buf2) {
    return is_up(buf2, buf1);
}

int main() {
    FILE *file = fopen("arrows.txt", "r");
    int file_len;

    fseek(file, 0L, SEEK_END);
    file_len = ftell(file);
    rewind(file);

    int lines = (file_len+1) / 12;

    // Rotations, lines, chars
    char squares[4][lines][9];

    char buffer[file_len+1];


    for(int i = 0; i < lines; i++) {
        int count = 0;
        for(int j = 0; j < 12; j++) {
            int val = fgetc(file);

            if(val != ',')
                squares[0][i][count++] = val;
                
        }
        squares[0][i][8] = '\0';
    }

    fclose(file);

    /*
    for(int i = 0; i < lines; i++) {
        for(int j = 0; j < 8; j++) {
            std::cout << squares[0][i][j];
        }
        std::cout << std::endl;
    }
    */

    //return 0;

    // Calculate rotations
    for(int i = 0; i < 4; i++) {
        for(int j = 0; j < lines; j++) {
            rotate_right(squares[i][j], squares[i+1][j]);
        }
        //std::cerr << "\nGroup " << (i+1) << " finished\n\n";
    }    

    int pair_counts[26];
    for(int i = 0; i < 26; i++)
        pair_counts[i] = 0;

    // Note: Count may be unreliable if a piece has both a start and end of the same type
    for(int i = 0; i < lines; i++) {
        for(int j = 0; j < 8; j+=2) {
            int letter = squares[0][i][j] - 'A';
            int number = squares[0][i][j+1];
            
            if(number == '1')
                pair_counts[letter]++;
            else
                pair_counts[letter]--;
        }
    }

    // Put these on the edges
    for(int i = 0; i < 26; i++) {
        if(pair_counts[i] < 0)
            std::cout << "Letter " << (char)('a' + i) << " has " << -1*pair_counts[i] << " more starts than ends\n";
        else if(pair_counts[i] > 0)
            std::cout << "Letter " << (char)('a' + i) << " has " << pair_counts[i] << " less starts than ends\n";
    }

    char *board[9];

}
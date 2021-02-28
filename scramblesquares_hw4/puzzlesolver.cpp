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

int num_from_addr(int lines, char *addr, char (*squares1)[9], char (*squares2)[9], char (*squares3)[9], char (*squares4)[9]) {
    for(int i = 0; i < lines; i++) {
        if(squares1[i] == addr || squares2[i] == addr || squares3[i] == addr || squares4[i] == addr)
            return i+1;
    }

    return -1;
}

void print_board(char *board[9], int lines, char (*squares1)[9], char (*squares2)[9], char (*squares3)[9], char (*squares4)[9]) {
    // Top, LR, Bottom
    /*
    std::cout << "*****************************\n";
    std::cout << num_from_addr(lines, board[3], squares1, squares2, squares3, squares4) << std::endl;
    for(int i = 0; i < 9; i++) {
        //std::cout << board[0] << std::endl;
        printf("%s\n", board[i]);
    }
    std::cout << "\n*****************************\n";
    */

    for(int i = 0; i < 7; i += 3) {
        std::cout << "+--------+--------+--------+\n";
        int num1 = num_from_addr(lines, board[i], squares1, squares2, squares3, squares4),
            num2 = num_from_addr(lines, board[i+1], squares1, squares2, squares3, squares4),
            num3 = num_from_addr(lines, board[i+2], squares1, squares2, squares3, squares4);

        std::cout << "|" << num1 << "  " << board[i][0] << board[i][1] << "   |" << num2 << "  " << board[i+1][0] << board[i+1][1] << "   |" << num3 << "  " << board[i+2][0] << board[i+2][1] << "   |\n";
        std::cout << "|" << board[i][6] << board[i][7] << "    " << board[i][2] << board[i][3] << "|" << board[i+1][6] << board[i+1][7] << "    " << board[i+1][2] << board[i+1][3] << "|" << board[i+2][6] << board[i+2][7] << "    " << board[i+2][2] << board[i+2][3] << "|\n";
        std::cout << "|   " << board[i][4] << board[i][5] << "   |   " << board[i+1][4] << board[i+1][5] << "   |   " << board[i+2][4] << board[i+2][5] << "   |\n";
    }
    std::cout << "+--------+--------+--------+\n";
    

    std::cout << std::endl;
}

void test(int width, char (*squares)[9]) {

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

    std::cout << "Input tiles:\n";
    for(int i = 0; i < lines; i++) {
        std::cout << (i+1);
        std::cout << ". <";
        for(int j = 0; j < 6; j += 2)
            std::cout << squares[0][i][j] << squares[0][i][j+1] << ", ";
        std::cout << squares[0][i][6] << squares[0][i][7] << ">\n";
    }

    // Array of pointers to squares
    char *board[9];
    board[0] = squares[0][0];
    board[1] = squares[0][1];
    board[2] = squares[0][2];
    board[3] = squares[0][3];
    board[4] = squares[0][4];
    board[5] = squares[0][5];
    board[6] = squares[0][6];
    board[7] = squares[0][7];
    board[8] = squares[0][8];

    print_board(board, lines, squares[0], squares[1], squares[2], squares[3]);
}
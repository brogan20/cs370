#include <fstream>
#include <sstream>
#include <string>
#include <iostream>
#include <vector>

std::vector<char **> g_solutions;

void rotate_right(char *buf1, char *buf2)
{
    //std::cerr << "Rotating " << buf1 << " to ";

    buf2[0] = buf1[6];
    buf2[1] = buf1[7];
    buf2[2] = buf1[0];
    buf2[3] = buf1[1];
    buf2[4] = buf1[2];
    buf2[5] = buf1[3];
    buf2[6] = buf1[4];
    buf2[7] = buf1[5];
    buf2[8] = buf1[8];
    buf2[9] = buf1[9] + 1;
    buf2[10] = '\0';

    //std::cerr << buf2 << std::endl;
}

// Is buf2 to the left of buf1
int is_left(char *buf1, char *buf2)
{
    return buf1[7] == '9' || buf2[3] == '9' || buf1[6] == buf2[2] && buf1[7] != buf2[3];
}

// Is buf2 to the right of buf1
int is_right(char *buf1, char *buf2)
{
    return is_left(buf2, buf1);
}

// Is buf2 above buf1
int is_up(char *buf1, char *buf2)
{
    return buf1[1] == '9' || buf2[5] == '9' || buf1[0] == buf2[4] && buf1[1] != buf2[5];
}

// Is buf2 below buf1
int is_down(char *buf1, char *buf2)
{
    return is_up(buf2, buf1);
}

int is_valid_board(char *board[11])
{
    char *center = board[4];

    // Compare center to adjacencies
    if (!is_up(center, board[1]) || !is_down(center, board[7]) || !is_left(center, board[3]) || !is_right(center, board[5]))
        return 0;

    // Compare corners
    if (!is_right(board[0], board[1]) || !is_down(board[0], board[3]))
        return 0;
    if (!is_left(board[2], board[1]) || !is_down(board[2], board[5]))
        return 0;
    if (!is_up(board[6], board[3]) || !is_right(board[6], board[7]))
        return 0;
    if (!is_up(board[8], board[5]) || !is_left(board[8], board[7]))
        return 0;

    return 1;
}

inline int num_from_addr(char *addr) {
    return addr[8] + 1;
}

void print_board(char *board[9], int lines, char (*squares1)[11], char (*squares2)[11], char (*squares3)[11], char (*squares4)[11])
{
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
    for (int i = 0; i < 9; i++)
    {
        //if(board[i][1] == '9')
        //    return;
    }

    for (int i = 0; i < 7; i += 3)
    {
        std::cout << "+--------+--------+--------+\n";
        int num1 = num_from_addr(board[i]),
            num2 = num_from_addr(board[i + 1]),
            num3 = num_from_addr(board[i + 2]);

        std::cout << "|" << num1 << "  " << board[i][0] << board[i][1] << "   |" << num2 << "  " << board[i + 1][0] << board[i + 1][1] << "   |" << num3 << "  " << board[i + 2][0] << board[i + 2][1] << "   |\n";
        std::cout << "|" << board[i][6] << board[i][7] << "    " << board[i][2] << board[i][3] << "|" << board[i + 1][6] << board[i + 1][7] << "    " << board[i + 1][2] << board[i + 1][3] << "|" << board[i + 2][6] << board[i + 2][7] << "    " << board[i + 2][2] << board[i + 2][3] << "|\n";
        std::cout << "|   " << board[i][4] << board[i][5] << "   |   " << board[i + 1][4] << board[i + 1][5] << "   |   " << board[i + 2][4] << board[i + 2][5] << "   |\n";
    }
    std::cout << "+--------+--------+--------+\n";

    std::cout << std::endl;
}

// checks if two boards are rotated versions of each other
bool are_rotations(char *board[9], char *board2[9], char (*squares1)[11], char (*squares2)[11], char (*squares3)[11], char (*squares4)[11]) {
    return  (board[0][8] == board2[2][8] && board[1][8] == board2[5][8] &&
            board[2][8] == board2[8][8] && board[3][8] == board2[1][8] &&
            board[4][8] == board2[4][8] && board[5][8] == board2[7][8] &&
            board[6][8] == board2[0][8] && board[7][8] == board2[3][8] &&
            board[8][8] == board2[6][8]) ||

            (board[0][8] == board2[8][8] && board[1][8] == board2[7][8] &&
            board[2][8] == board2[6][8] && board[3][8] == board2[5][8] &&
            board[4][8] == board2[4][8] && board[5][8] == board2[3][8] &&
            board[6][8] == board2[2][8] && board[7][8] == board2[1][8] &&
            board[8][8] == board2[0][8]) || 

            (board[0][8] == board2[6][8] && board[1][8] == board2[3][8] &&
            board[2][8] == board2[0][8] && board[3][8] == board2[7][8] &&
            board[4][8] == board2[4][8] && board[5][8] == board2[1][8] &&
            board[6][8] == board2[8][8] && board[7][8] == board2[5][8] &&
            board[8][8] == board2[2][8]);
}

// checks if the first board is smaller than the second
// means if the first board has lowered number squares
bool is_smaller(char *board[9], char *board2[9]) {
    for (int i=0; i<9; i++) {
        if (board[i][8] < board2[i][8]) {
            return true;
        }
        else if (board[i][8] > board2[i][8]) {
            return false;
        }
    }
    // we shouldn't reach this statement
    // that would mean both boards are identical
    return true;
}
void solve(char *board[9], std::vector<bool> used, const int index, const int lines, char (*squares1)[11], char (*squares2)[11], char (*squares3)[11], char (*squares4)[11])
{
    // Place one down, check if valid, continue
    bool done = true;
    int modInd = index % 9;
    //std::cerr << "Doing: " << modInd << std::endl;

    for (int i = 0; i < used.size(); i++)
    {
        if (!used[i])
        {
            //std::cerr << "Fart\n";
            done = false;
            break;
        }
    }
    if (done)
    {
        if(!is_valid_board(board))
            return;

        //make a new copy of the board
        char **copy = new char*[9];
        for(int i = 0; i < 9; i++) {
            copy[i] = board[i];
        }

        //push solution to empty list
        if (g_solutions.empty()) {
            g_solutions.push_back(copy);
        } else {
            int idx = 0;
            bool fresh = true;
            for (char ** sol: g_solutions) {
                // if the new board is a rotation of a solution
                if (are_rotations(copy, sol, squares1, squares2, squares3, squares4)) {
                    fresh = false;
                    // if the new board is smaller than the solution, replace the existing solution
                    if (is_smaller(copy, sol)) {
                        delete [] g_solutions.at(idx);
                        g_solutions.at(idx) = copy;
                    }
                        
                    break;
                } 
                idx++;
            }
            //if the new board is actually new, insert it in order
            if (fresh) {
                idx = 0;
                for (char ** sol: g_solutions) {
                    if (is_smaller(copy, sol)) {
                        g_solutions.insert(g_solutions.begin()+idx, copy);
                        break;
                    }
                    idx++;
                }
                // if we haven't inserted the solution yet, put it at the end
                if (idx == g_solutions.size())
                    g_solutions.push_back(copy);
            }
            else
                delete [] copy;
        }
        return;
    }

    for (int i = 0; i < used.size(); i++)
    {
        //std::cerr << "Checking: " << i << std::endl;
        if (!used[i])
        {
            //std::cerr << "Good: " << i << std::endl;
            used[i] = true;

            board[modInd] = squares1[i];
            if (is_valid_board(board))
                solve(board, used, index + 1, lines, squares1, squares2, squares3, squares4);
            //else
            //    std::cerr << "Bad1\n";

            board[modInd] = squares2[i];
            if (is_valid_board(board))
                solve(board, used, index + 1, lines, squares1, squares2, squares3, squares4);
            //else
            //    std::cerr << "Bad2\n";

            board[modInd] = squares3[i];
            if (is_valid_board(board))
                solve(board, used, index + 1, lines, squares1, squares2, squares3, squares4);
            //else
            //    std::cerr << "Bad3\n";

            board[modInd] = squares4[i];
            if (is_valid_board(board))
                solve(board, used, index + 1, lines, squares1, squares2, squares3, squares4);
            //else
            //    std::cerr << "Bad4\n";

            // May not be necessary
            board[modInd] = squares1[9];

            used[i] = false;
        }
    }
} 

int main()
{
    FILE *file = fopen("arrows.txt", "r");

    int lines = 9;

    // Rotations, lines, chars
    char squares[4][lines + 1][11];

    for (int i = 0; i < lines; i++)
    {
        int count = 0;
        for (int j = 0; j < 12; j++)
        {
            int val = fgetc(file);

            if (val != ',')
                squares[0][i][count++] = val;
        }
        // 8th bit is square number - 1
        squares[0][i][8] = i;
        // 9th bit is which rotation it is
        squares[0][i][9] = '0';
        squares[0][i][10] = '\0';
    }

    // Hacky process to give us default 'dummy' values later
    squares[0][lines][0] = 'Z';
    squares[0][lines][1] = '9';
    squares[0][lines][2] = 'Z';
    squares[0][lines][3] = '9';
    squares[0][lines][4] = 'Z';
    squares[0][lines][5] = '9';
    squares[0][lines][6] = 'Z';
    squares[0][lines][7] = '9';
    squares[0][lines][8] =  9;
    squares[0][lines][9] = '0';
    squares[0][lines][10] = '\0';

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
    for (int i = 0; i < 4; i++)
    {
        for (int j = 0; j < lines; j++)
        {
            rotate_right(squares[i][j], squares[i + 1][j]);
        }
        //std::cerr << "\nGroup " << (i+1) << " finished\n\n";
    }

    // for (int i = 0; i < 4; i++)
    // {
    //     std::cout << squares[i][0][9] << std::endl;
    // }

    int pair_counts[26];
    for (int i = 0; i < 26; i++)
        pair_counts[i] = 0;

    // Note: Count may be unreliable if a piece has both a start and end of the same type
    for (int i = 0; i < lines; i++)
    {
        for (int j = 0; j < 8; j += 2)
        {
            int letter = squares[0][i][j] - 'A';
            int number = squares[0][i][j + 1];

            if (number == '1')
                pair_counts[letter]++;
            else
                pair_counts[letter]--;
        }
    }

    // Put these on the edges
    /*
    for (int i = 0; i < 26; i++)
    {
        if (pair_counts[i] < 0)
            std::cout << "Letter " << (char)('A' + i) << " has " << -1 * pair_counts[i] << " more starts than ends\n";
        else if (pair_counts[i] > 0)
            std::cout << "Letter " << (char)('A' + i) << " has " << pair_counts[i] << " less starts than ends\n";
    } */

    std::cout << "Input tiles:\n";
    for (int i = 0; i < lines; i++)
    {
        std::cout << (i + 1);
        std::cout << ". <";
        for (int j = 0; j < 6; j += 2)
            std::cout << squares[0][i][j] << squares[0][i][j + 1] << ", ";
        std::cout << squares[0][i][6] << squares[0][i][7] << ">\n";
    }
    std::cout << "\n";

    // Array of pointers to squares
    char *board[9];
    board[0] = squares[0][9];
    board[1] = squares[0][9];
    board[2] = squares[0][9];
    board[3] = squares[0][9];
    board[4] = squares[0][9];
    board[5] = squares[0][9];
    board[6] = squares[0][9];
    board[7] = squares[0][9];
    board[8] = squares[0][9];

    // Bit array specialization
    std::vector<bool> used;
    used.reserve(9);
    for (int i = 0; i < 9; i++)
    {
        used.push_back(false);
    }

    //print_board(board, lines, squares[0], squares[1], squares[2], squares[3]);

    solve(board, used, lines, 4, squares[0], squares[1], squares[2], squares[3]);

    // print out all the unique solutions
    if (g_solutions.size() == 0)
        std::cout << "No solution found.\n";
    else if (g_solutions.size() == 1)
        std::cout << "1 unique solution found:\n";
    else
        std::cout << g_solutions.size() << " unique solutions found:\n";

    for (char ** sol: g_solutions) {
        print_board(sol, lines, squares[0], squares[1], squares[2], squares[3]);
        delete [] sol;
    }

    //print_board(board, lines, squares[0], squares[1], squares[2], squares[3]);
}
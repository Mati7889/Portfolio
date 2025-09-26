/*
* Program: Sokoban
 * Author:  Mateusz Gnat
 * Date:    07.01.2025
 * Purpose: Allows playing the "Sokoban" game, where the player's goal
 *          is to push boxes onto target spots on the board
 *          without getting them stuck along the way.
 *          Handles move undo using a stack.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <assert.h>

// Movement directions definitions
#define UP '8'
#define DOWN '2'
#define LEFT '4'
#define RIGHT '6'

// Stack for undoing moves
typedef struct {
    char ***stack;
    int top;
    int size;
} Stack;

// Stack initialization
void initStack(Stack *s, int start_size) {
    s->stack = malloc(sizeof(char **) * (size_t) start_size);
    s->top = -1;
    s->size = start_size;
}

// Checks if the stack is empty
bool is_empty(Stack *s) {
    return s->top == -1;
}

// Increase the stack size
void increase_stack(Stack *s) {
    s->size += 2;
    s->stack = realloc(s->stack, sizeof(char **) * (size_t) s->size);
    if (!s->stack) exit(-1);
}

// Adds an element to the stack
bool push(Stack *s, char **board, const int w, const int *length) {
    if (s->top == s->size - 1) increase_stack(s);
    s->top++;
    s->stack[s->top] = malloc((size_t) w * sizeof(char *));
    for (int i = 0; i < w; i++) {
        s->stack[s->top][i] = malloc((size_t) (length[i] + 1) * sizeof(char));
        strcpy(s->stack[s->top][i], board[i]);
    }
    return true;
}

// Deletes stack element
bool pop(Stack *s, const int w) {
    if (is_empty(s)) return false;
    for (int i = 0; i < w; i++) free(s->stack[s->top][i]);
    free(s->stack[s->top]);
    s->top--;
    return true;
}

// Frees the memory used by the stack
void freeStack(Stack *s, const int w) {
    while (!is_empty(s)) pop(s, w);
    free(s->stack);
}

// Prints the board
void print_board(char **board, const int w) {
    for (int i = 0; i < w; i++) {
        printf("%s\n", board[i]);
    }
}

// Finds the box and returns its coordinates
int *find_chest(char **board, const char c, const int *length, const int w) {
    int *chest = malloc(sizeof(int) * 2);
    for (int y = 0; y < w; y++) {
        for (int x = 0; x < length[y]; x++) {
            if (board[y][x] == c
                || board[y][x] == (char) (c - 'a' + 'A')) {
                chest[0] = y;
                chest[1] = x;
                return chest;
            }
        }
    }
    free(chest);
    return NULL;
}

// Recursively marks all reachable tiles from the player's position
void bfs(char **board, char **copy, const int w, const int *player,
    int *length) {
    int directions[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    copy[player[0]][player[1]] = '1';

    for (int i = 0; i < 4; i++) {
        int newY = player[0] + directions[i][0];
        int newX = player[1] + directions[i][1];
        int new_player[2] = {newY, newX};
        if (newY >= 0 && newY < w && newX >= 0 && newX < length[newY]
            && (board[newY][newX] == '-' || board[newY][newX] == '+')
            && copy[newY][newX] != '1') {
            bfs(board, copy, w, new_player, length);
        }
    }
}

// Creates a board copy initialized with '0'
char **empty_board(int const *length, const int rows) {
    char **copy01 = malloc((size_t) rows * sizeof(char *));
    assert(copy01 != NULL);
    for (int i = 0; i < rows; i++) {
        copy01[i] = malloc((size_t) (length[i] + 1) * sizeof(char));
        assert(copy01[i] != NULL);
        for (int j = 0; j < length[i]; j++) {
            copy01[i][j] = '0';
        }
        copy01[i][length[i]] = '\0';
    }
    return copy01;
}

// Returns a board marking reachable tiles with '1'
char **bfs_copy01(char **board, int *length, int *player, const int rows) {
    char **copy01 = empty_board(length, rows);
    bfs(board, copy01, rows, player, length);
    return copy01;
}

// Free memory allocated to the move copy
void free_copy01(char **copy01, const int rows) {
    for (int i = 0; i < rows; i++) {
        free(copy01[i]);
    }
    free(copy01);
}

// Checks if box is movable
bool valid_symbol (char **board, const int chest[], int y, int x) {
    char symbol = board[chest[0] + y][chest[1] + x];
    return symbol == '-' || symbol == '+' || symbol == '@' || symbol == '*';
}

// Validates the player's move based on the position of the player and the boxes
bool move_check(char **board, const int *chest, char **copy01, const char c,
    const int length[], int w) {
    if (c == DOWN) {
        if (chest[0] + 1 < w && chest[0] - 1 >= 0
            && valid_symbol(board, chest, 1, 0 )
            && copy01[chest[0] - 1][chest[1]] == '1') return true;
    } else if (c == UP) {
        if (chest[0] + 1 < w && chest[0] - 1 >= 0
            && valid_symbol(board, chest, -1, 0)
            && copy01[chest[0] + 1][chest[1]] == '1') return true;
    } else if (c == RIGHT) {
        if (chest[1] + 1 < length[chest[0]] && chest[1] - 1 >= 0
            && valid_symbol(board, chest, 0, 1)
            && copy01[chest[0]][chest[1] - 1] == '1') return true;
    } else if (c == LEFT) {
        if (chest[1] + 1 < length[chest[0]] && chest[1] - 1 >= 0
            && valid_symbol(board, chest, 0, -1)
            && copy01[chest[0]][chest[1] + 1] == '1') return true;
    }
    return false;
}

// Updated the board after successful move
void board_update(char **board, const int chest[], const char *c, char prev) {
    if (*c == DOWN) {
        if (board[chest[0] + 1][chest[1]] == '+')
            prev = (char) (prev - 'a' + 'A');
        board[chest[0] + 1][chest[1]] = prev;
    } else if (*c == UP) {
        if (board[chest[0] - 1][chest[1]] == '+')
            prev = (char) (prev - 'a' + 'A');
        board[chest[0] - 1][chest[1]] = prev;
    } else if (*c == RIGHT) {
        if (board[chest[0]][chest[1] + 1] == '+')
            prev = (char) (prev - 'a' + 'A');
        board[chest[0]][chest[1] + 1] = prev;
    } else if (*c == LEFT) {
        if (board[chest[0]][chest[1] - 1] == '+')
            prev = (char) (prev - 'a' + 'A');
        board[chest[0]][chest[1] - 1] = prev;
    }
}

// Returns player coordinates
int *find_player(char **board, int rows, const int *length) {
    int *player = malloc(sizeof(int) * 2);
    for (int y = 0; y < rows; y++) {
        for (int x = 0; x < length[y]; x++) {
            if (board[y][x] == '@' || board[y][x] == '*') {
                player[0] = y;
                player[1] = x;
                return player;
            }
        }
    }
    free(player);
    return NULL;
}

// Visualizes the player's move
void swap_p_c(char **board, const int chest[], const int player[]) {
    if (board[player[0]][player[1]] == '@') board[player[0]][player[1]] = '-';
    if (board[player[0]][player[1]] == '*') board[player[0]][player[1]] = '+';
    if (board[chest[0]][chest[1]] >= 'a' && board[chest[0]][chest[1]] <= 'z')
        board[chest[0]][chest[1]] = '@';
    if (board[chest[0]][chest[1]] >= 'A' && board[chest[0]][chest[1]] <= 'Z')
        board[chest[0]][chest[1]] = '*';
}

// Checks for a letter in the input and processes the player's move
void chest_found(char **board, char *c, int length[], int rows, Stack **s) {
    int *chest = find_chest(board, *c, length, rows);
    int *player = find_player(board, rows, length);
    char **copy01 = bfs_copy01(board, length, player, rows);
    assert(copy01 != NULL);
    char prev = *c;
    *c = (char) getchar();
    if (chest != NULL && move_check(board, chest, copy01, *c, length, rows)) {
        swap_p_c(board, chest, player);
        board_update(board, chest, c, prev);
        push(*s, board, rows, length);
    }
    free(chest);
    free_copy01(copy01, rows);
    free(player);
}

// Parses data
void data_parsing(char *c, char **board, Stack *s, int *length, const int w) {
    print_board(board, w);
    while ((*c = (char) getchar()) != '.') {
        if (*c == '\n') {
            print_board(board, w);
        } else if (*c == '0') {
            if (s->top != 0) {
                pop(s, w);
                for (int i = 0; i < w; i++) {
                    strcpy(board[i], s->stack[s->top][i]);
                }
            }
        } else chest_found(board, c, length, w, &s);
    }
}

// Frees the board's memory
void free_board(char **board, const int rows) {
    for (int i = 0; i < rows; i++) {
        free(board[i]);
    }
    free(board);
}

// Loads the board
char **init_board(int **length, char *c, int *rows) {
    char prev = '\0';
    int maxCol = 5, columns = 0;
    bool board_end = false;
    char **board = NULL;
    char *row = malloc(sizeof(char) * (size_t) (maxCol + 1));
    assert(row != NULL);
    *rows = 0;

    while (!board_end) {
        *c = (char) getchar();
        if (*c == '\n') {
            if (prev == '\n') {
                board_end = true;
            } else {
                row[columns] = '\0';
                board = realloc(board, sizeof(char *) *
                    (size_t) (*rows + 1));
                assert(board != NULL);

                board[*rows] = row;
                *length = realloc(*length, sizeof(int) *
                    (size_t) (*rows + 1));
                assert(*length != NULL);

                (*length)[*rows] = columns;
                (*rows)++;
                columns = 0;
                row = malloc(sizeof(char) * (size_t) (maxCol + 1));
            }
            assert(row != NULL);
        } else {
            if (columns >= maxCol) {
                maxCol += maxCol;
                row = realloc(row, sizeof(char) * (size_t) (maxCol + 1));
                assert(row != NULL);
            }
            row[columns++] = *c;
        }
        prev = *c;
    }

    board = realloc(board, sizeof(char *) * (size_t) (*rows + 1));
    assert(board != NULL);
    board[*rows] = NULL;
    free(row);
    return board;
}

// Calls main functions
int main(void) {
    int *length = NULL;
    char c;
    int rows;
    char **board = init_board(&length, &c, &rows);
    Stack s;
    initStack(&s, 4);
    push(&s, board, rows, length);
    data_parsing(&c, board, &s, length, rows);
    free_board(board, rows);
    free(length);
    freeStack(&s, rows);
    return 0;
}

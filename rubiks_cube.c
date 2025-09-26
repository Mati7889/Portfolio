/*
* Program: Rubik's cube
* Author:  Mateusz Gnat
* Date:    10.12.2024
* Purpose: Simulates solving Rubik's cubes of any size.
*/


#include <stdio.h>

#ifndef N
#define N 5
#endif
// Wall 'up'
#define U 0
// Wall 'left'
#define L 1
// Wall 'front'
#define F 2
// Wall 'right'
#define R 3
// Wall 'back'
#define B 4
// Wall 'down'
#define D 5

// Fills an array representing the cube
void create_cube(int cube[][N][N]) {
    for (int i = 0; i < 6; i++) {
        for (int j = 0; j < N; j++) {
            for (int k = 0; k < N; k++) {
                cube[i][j][k] = i;
            }
        }
    }
}

// Creates spaces between 'up' and 'down' walls
void spaces(void) {
    for (int j = 0; j < N + 1; j++) {
        printf(" ");
    }
}

// Prints 'up' and 'down' walls
void print_u_d(int wall, int cube[][N][N]) {
    for (int i = 0; i < N; i++) {
        spaces();
        for (int j = 0; j < N; j++) {
            printf("%d", cube[wall][i][j]);
        }
        printf("\n");
    }
}

// Prints the remaining walls (left, front, right, back)
void print_l_f_r_b(int cube[][N][N]) {
    for (int j = 0; j < N; j++) {
        for (int i = 1; i < 5; i++) {
            for (int k = 0; k < N; k++) {
                printf("%d", cube[i][j][k]);
            }
            if (i < 4) {
                printf("|");
            }
        }
        printf("\n");
    }
}

// Prints whole cube
void print_cube(int cube[][N][N]) {
    printf("\n");
    print_u_d(U, cube);
    print_l_f_r_b(cube);
    print_u_d(D, cube);
}

// Reads the layer numver
int layer_num(char *i) {
    int layer = 0;
    while (*i >= '0' && *i <= '9') {
        layer = layer * 10 + (*i - '0');
        *i = (char) getchar();
    }
    if (layer == 0) layer = 1;
    return layer;
}

// Reads the wall number
int get_wall(char i) {
    switch (i) {
        case 'u':
            return U;
        case 'l':
            return L;
        case 'f':
            return F;
        case 'r':
            return R;
        case 'b':
            return B;
        case 'd':
            return D;
        default:
            return -1;
    }
}

// Reads the angle
int get_angle(char *i) {
    *i = (char) getchar();

    switch (*i) {
        case '\'':
            return 3;
        case '\"':
            return 2;
        default:
            ungetc(*i, stdin);
            return 1;
    }
}


// Rotates the wall 'facing' the player
void wall_rotate(int wall, int cube[][N][N]) {
    int tmp[N][N];
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            tmp[i][j] = cube[wall][i][j];
        }
    }
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            cube[wall][j][N - 1 - i] = tmp[i][j];
        }
    }
}

// Rotates the edges of neighbouring walls and 'up' wall by given angle
void up_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[B][i][j], cube[R][i][j], cube[F][i][j],
                    cube[L][i][j]
                };
                cube[R][i][j] = tmp[0];
                cube[F][i][j] = tmp[1];
                cube[L][i][j] = tmp[2];
                cube[B][i][j] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Rotates the edges of neighbouring walls and 'left' wall by given angle
void left_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[U][j][i], cube[F][j][i], cube[D][j][i],
                    cube[B][N - 1 - j][N - 1 - i]
                };
                cube[F][j][i] = tmp[0];
                cube[D][j][i] = tmp[1];
                cube[B][N - 1 - j][N - 1 - i] = tmp[2];
                cube[U][j][i] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Rotates the edges of neighbouring walls and 'front' wall by given angle
void front_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[U][N - 1 - i][j], cube[R][j][i],
                    cube[D][i][N - 1 - j], cube[L][N - 1 - j][N - 1 - i]
                };
                cube[R][j][i] = tmp[0];
                cube[D][i][N - 1 - j] = tmp[1];
                cube[L][N - 1 - j][N - 1 - i] = tmp[2];
                cube[U][N - 1 - i][j] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Rotates the edges of neighbouring walls and 'right' wall by given angle
void right_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[U][N - 1 - j][N - 1 - i], cube[B][j][i],
                    cube[D][N - 1 - j][N - 1 - i],
                    cube[F][N - 1 - j][N - 1 - i]
                };
                cube[B][j][i] = tmp[0];
                cube[D][N - 1 - j][N - 1 - i] = tmp[1];
                cube[F][N - 1 - j][N - 1 - i] = tmp[2];
                cube[U][N - 1 - j][N - 1 - i] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Rotates the edges of neighbouring walls and 'back' wall by given angle
void back_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[U][i][N - 1 - j], cube[L][j][i],
                    cube[D][N - 1 - i][j], cube[R][N - 1 - j][N - 1 - i]
                };
                cube[L][j][i] = tmp[0];
                cube[D][N - 1 - i][j] = tmp[1];
                cube[R][N - 1 - j][N - 1 - i] = tmp[2];
                cube[U][i][N - 1 - j] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Rotates the edges of neighbouring walls and 'down' wall by given angle
void down_rotate(int layer, int cube[][N][N], int angle, int wall) {
    for (int k = 0; k < angle; k++) {
        for (int i = 0; i < layer; i++) {
            for (int j = 0; j < N; j++) {
                int tmp[4] = {
                    cube[L][N - 1 - i][j], cube[F][N - 1 - i][j],
                    cube[R][N - 1 - i][j], cube[B][N - 1 - i][j]
                };
                cube[F][N - 1 - i][j] = tmp[0];
                cube[R][N - 1 - i][j] = tmp[1];
                cube[B][N - 1 - i][j] = tmp[2];
                cube[L][N - 1 - i][j] = tmp[3];
            }
        }
        wall_rotate(wall, cube);
    }
}

// Roatates opposite wall if layer number equals N
void rev_rotate(int wall, int angle, int layer, int cube[][N][N]) {
    int opposite[] = {D, R, B, L, F, U};
    if (layer == N) {
        for (int z = 0; z < 4 - angle; z++) wall_rotate(opposite[wall], cube);
    }
}

// Handles all rotations depending on the given wall
void actions(int wall, int layer, int angle, int cube[][N][N]) {
    switch (wall) {
        case U:
            up_rotate(layer, cube, angle, wall); break;
        case L:
            left_rotate(layer, cube, angle, wall); break;
        case F:
            front_rotate(layer, cube, angle, wall); break;
        case R:
            right_rotate(layer, cube, angle, wall); break;
        case B:
            back_rotate(layer, cube, angle, wall); break;
        case D:
            down_rotate(layer, cube, angle, wall); break;
        default:
            break;
    }
}

// Parses data and calls operations on the cube
void data_parsing(int cube[6][N][N]) {
    char i;
    while ((i = (char) getchar()) != '.') {
        if (i == '\n') {
            print_cube(cube);
        } else {
            int layer = layer_num(&i);
            int wall = get_wall(i);
            int angle = get_angle(&i);
            actions(wall, layer, angle, cube);
            rev_rotate(wall, angle, layer, cube);
        }
    }
}

int main(void) {
    int cube[6][N][N];
    create_cube(cube);
    data_parsing(cube);
    return 0;
}

#include <assert.h>
#include <limits.h>
#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char* readline();
char** split_string(char*);

/*
 * Complete the contacts function below.
 */

/*
 * Please store the size of the integer array to be returned in result_count pointer. For example,
 * int a[3] = {1, 2, 3};
 *
 * *result_count = 3;
 *
 * return a;
 *
 */
int* contacts(int queries_rows, int queries_columns, char*** queries, int* result_count) {
    /*
     * Write your code here.
     */
    fprintf(stderr, "I will be printed immediately: %d\n", queries_rows);
    
    for(int i = 0; i < queries_rows; i++) {
        char **query = queries[i];
        
        for(int j = 0; j < queries_columns; j++) {
            char *q = query[j];
            
            for(int k = 0; k < strlen(q); k++) {
                fprintf(stderr, "%c", q[k]);
            }
            fprintf(stderr, "\n");
        }
    }
    
    return 0;
}

int main()
{
    FILE* fptr = fopen(getenv("OUTPUT_PATH"), "w");

    char* queries_rows_endptr;
    char* queries_rows_str = readline();
    int queries_rows = strtol(queries_rows_str, &queries_rows_endptr, 10);

    if (queries_rows_endptr == queries_rows_str || *queries_rows_endptr != '\0') { exit(EXIT_FAILURE); }

    char*** queries = malloc(queries_rows * sizeof(char**));

    for (int queries_row_itr = 0; queries_row_itr < queries_rows; queries_row_itr++) {
        queries[queries_row_itr] = malloc(2 * (sizeof(char*)));

        char** queries_item_temp = split_string(readline());

        for (int queries_column_itr = 0; queries_column_itr < 2; queries_column_itr++) {
            char* queries_item = queries_item_temp[queries_column_itr];

            queries[queries_row_itr][queries_column_itr] = queries_item;
        }
    }

    int result_count;
    int* result = contacts(queries_rows, 2, queries, &result_count);

    for (int result_itr = 0; result_itr < result_count; result_itr++) {
        fprintf(fptr, "%d", result[result_itr]);

        if (result_itr != result_count - 1) {
            fprintf(fptr, "\n");
        }
    }

    fprintf(fptr, "\n");

    fclose(fptr);

    return 0;
}

char* readline() {
    size_t alloc_length = 1024;
    size_t data_length = 0;
    char* data = malloc(alloc_length);

    while (true) {
        char* cursor = data + data_length;
        char* line = fgets(cursor, alloc_length - data_length, stdin);

        if (!line) { break; }

        data_length += strlen(cursor);

        if (data_length < alloc_length - 1 || data[data_length - 1] == '\n') { break; }

        size_t new_length = alloc_length << 1;
        data = realloc(data, new_length);

        if (!data) { break; }

        alloc_length = new_length;
    }

    if (data[data_length - 1] == '\n') {
        data[data_length - 1] = '\0';
    }

    data = realloc(data, data_length);

    return data;
}

char** split_string(char* str) {
    char** splits = NULL;
    char* token = strtok(str, " ");

    int spaces = 0;

    while (token) {
        splits = realloc(splits, sizeof(char*) * ++spaces);
        if (!splits) {
            return splits;
        }

        splits[spaces - 1] = token;

        token = strtok(NULL, " ");
    }

    return splits;
}
